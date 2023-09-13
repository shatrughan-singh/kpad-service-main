package com.kpad.app.serviceimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.kpad.app.dto.AgentDTO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.kpad.app.constant.KpadConstant;
import com.kpad.app.dto.DepartmentDTO;
import com.kpad.app.dto.FluxRecordDTO;
import com.kpad.app.dto.ServiceUrlDTO;
import com.kpad.app.repository.AgentConfigRepository;
import com.kpad.app.repository.DepartmentRepository;
import com.kpad.app.repository.ServiceUrlRepository;
import com.kpad.app.service.KpadService;
import com.kpad.exceptions.KpadException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpadServiceImpl implements KpadService {

	@Autowired
	private DepartmentRepository deptRepository;

	@Autowired
	private AgentConfigRepository agentConfigRepository;
	
	@Autowired
	private ServiceUrlRepository serviceUrlRepository;

	@Value("${influx.db.url}")
	private String influxDbUrl;
	
	@Value("${influx.db.token}")
	private String influxDbToken;	

	@Override
	public List<DepartmentDTO> getDepartmentDetails() throws KpadException {
		List<DepartmentDTO> departmentDTOList = new ArrayList<>();
		try {
			log.info("Start of getDepartmentDetails method");
			departmentDTOList = deptRepository.findAll();
			log.info("END of getDepartmentDetails method departmentDTOList = {} ", departmentDTOList);
		} catch (Exception e) {
			log.error("End of getDepartmentDetails, E xception while calling getDepartmentDetails method  ", e);
			throw new KpadException("400", "Exception while calling getDepartmentDetails", HttpStatus.BAD_REQUEST);
		}
		return departmentDTOList;
	}
	
	@Override
	public List<DepartmentDTO> getDepartmentCountDetails(String deptName) throws KpadException {
		List<DepartmentDTO> departmentDTOList = new ArrayList<>();
		try {
			log.info("Start of getDepartmentDetails method");
			List<ServiceUrlDTO> serviceUrlList = getServiceUrlList(deptName);
			Map<String, List<ServiceUrlDTO>> serviceUrlMap = serviceUrlList.stream()
					.collect(Collectors.groupingBy(ServiceUrlDTO::getDeptName));
			Integer onlineCount = 0;
			Integer totalOnlineCount = 0;
			Integer maintenanceCount = 0;
			Integer totalmaintenanceCount = 0;
			Integer totalOfflineCount = 0;
			for (Map.Entry<String, List<ServiceUrlDTO>> map : serviceUrlMap.entrySet()) {
				DepartmentDTO deptDto = new DepartmentDTO();
				deptDto.setTotalDeptServiceCount(serviceUrlList.size());
				for (ServiceUrlDTO serviceDto : map.getValue()) {
					if (serviceDto.getIsMaintenance() != null && serviceDto.getIsMaintenance()) {
						maintenanceCount = maintenanceCount + 1;
					} else {
						onlineCount = onlineCount + getDepartmentDetailsFromInfluxDB(serviceDto.getServiceUrl());
					}
				}
				deptDto.setOnlineCount(onlineCount);
				totalOnlineCount = totalOnlineCount + onlineCount;
				deptDto.setOfflineCount(map.getValue().size() - onlineCount);
				totalOfflineCount = totalOfflineCount + deptDto.getOfflineCount();
				deptDto.setMaintanceCount(maintenanceCount);
				totalmaintenanceCount = totalmaintenanceCount + maintenanceCount;
				deptDto.setDeptName(map.getKey());
				onlineCount = 0;
				departmentDTOList.add(deptDto);
			}
			if(departmentDTOList.size() > 0) {
				departmentDTOList.get(0).setTotalOfflineCount(totalOfflineCount);
				departmentDTOList.get(0).setTotalOnlineCount(totalOnlineCount);
				departmentDTOList.get(0).setTotalMaintenanceCount(totalmaintenanceCount);
			}

			log.info("onlineCount  ", onlineCount);
//			departmentDTOList.get(0).setOnlineCount(onlineCount);
			log.info("END of getDepartmentDetails method deptMap = {} ", departmentDTOList);
		} catch (Exception e) {
			log.error("End of getDepartmentDetails, E xception while calling getDepartmentDetails method  ", e);
			throw new KpadException("400", "Exception while calling getDepartmentDetails", HttpStatus.BAD_REQUEST);
		}
		return departmentDTOList;
	}

	@Override
	public Integer getDepartmentDetailsFromInfluxDB(String server) throws KpadException {
		List<DepartmentDTO> departmentDTOList = new ArrayList<>();
		List<FluxRecordDTO> fluxRecordDTOList = new ArrayList<>();
		Integer onlineCount = 0;
		try {
			char[] ch = new char[influxDbToken.length()];
			for (int i = 0; i < influxDbToken.length(); i++) {
				ch[i] = influxDbToken.charAt(i);
			}
			InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxDbUrl, ch, "TechQWare", "Kpad");
//	String flux = String.format("from(bucket: \"%s\")\n" + "      |> range(start: -24h)\n"
//			+ "      |> filter(fn: (r) => r[\"_measurement\"] == \"http_url_check\")\n"
//			+ "      |> filter(fn: (r) => r[\"_field\"] == \"result_code\")\n" + "      |> group()\n"
//			+ "      |> last()", "Kpad");

			String flux = String.format("from(bucket: \"%s\")\n" + "      |> range(start: -24h)\n"
					+ "      |> filter(fn: (r) => r[\"_measurement\"] == \"http_url_check\")\n"
					+ "      |> filter(fn: (r) => r[\"_field\"] == \"http_response_code\")\n"
					+ "      |> filter(fn: (r) => r[\"server\"] == \"" + server + "\")\n"
					+ "      |> filter(fn: (r) => r[\"status_code\"] == \"" + 200 + "\")\n"
					+ "      |> group()\n"
					+ "      |> last()",
					"Kpad");
			QueryApi queryApi = influxDBClient.getQueryApi();
			List<FluxTable> tables = queryApi.query(flux);
			log.info("tables size == {}", tables.size());
			for (FluxTable fluxTable : tables) {
				// Online count
				List<FluxRecord> records = fluxTable.getRecords();
				log.info("Record size == {}", records.size());
				onlineCount = records.size();
			}
			log.info("fluxRecordDTOList = {} ", fluxRecordDTOList);
			influxDBClient.close();
			log.info("END of getDepartmentDetails method departmentDTOList = {} ", departmentDTOList);
		} catch (Exception e) {
			log.error("End of getDepartmentDetails, Exception while calling getDepartmentDetails method  ", e);
			throw new KpadException("400", "Exception while calling getDepartmentDetails", HttpStatus.BAD_REQUEST);
		}
		return onlineCount;
	}
	
	@Override
	public List<DepartmentDTO> saveDepartmentDetails(List<DepartmentDTO> deptDtoList) throws KpadException {
		List<DepartmentDTO> detpSaveResult = new ArrayList<>();
		try {
			log.info("Start of saveDepartmentDetails method");
			detpSaveResult = deptRepository.saveAll(deptDtoList);
			log.info("detpSaveResult =  ", detpSaveResult);
		} catch (Exception e) {
			log.error("End of getDepartmentDetails, Exception while calling saveDepartmentDetails method  ", e);
			throw new KpadException("400", "Exception while calling saveDepartmentDetails", HttpStatus.BAD_REQUEST);
		}
		return detpSaveResult;
	}

	@Override
	public List<DepartmentDTO> agentUpdateConfig(List<DepartmentDTO> deptDtoList) throws KpadException {
		List<DepartmentDTO> detpSaveResult = new ArrayList<>();
		try {
			log.info("Start of saveDepartmentDetails method");
			detpSaveResult = deptRepository.saveAll(deptDtoList);
			log.info("detpSaveResult =  ", detpSaveResult);
		} catch (Exception e) {
			log.error("End of getDepartmentDetails, Exception while calling saveDepartmentDetails method  ", e);
			throw new KpadException("400", "Exception while calling saveDepartmentDetails", HttpStatus.BAD_REQUEST);
		}
		return detpSaveResult;
	}

	@Override
	public List<AgentDTO> updateTelegrafAgentConfig(AgentDTO agentDto) throws KpadException {
		List<AgentDTO> agentDtoList = new ArrayList<>();
		try {
			log.info("Start of updateTelegrafAgentConfig method");
			List<ServiceUrlDTO>  serviceUrlDtoList = getServiceUrlList("");
			final String serviceUrl = agentDto.getUpdatedServiceUrl();
			if (serviceUrlDtoList != null
					&& serviceUrlDtoList.stream().anyMatch(o -> o.getServiceUrl().equals(serviceUrl))) {
				throw new KpadException("400", "Service Url Already Present", HttpStatus.BAD_REQUEST);
			} else {
				serviceUrlDtoList = insertServiceUrlDetails(agentDto);
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n \r\n[[inputs.http_response]]\r\n");
			sb.append("urls = [\"" + agentDto.getUpdatedServiceUrl() + "\"]\r\n");
			sb.append("  follow_redirects = true\r\n" + "  interval = \"" + agentDto.getInterval() + "\"\r\n" + "  response_timeout = \"" + agentDto.getResponseTimeout() + "\"\r\n"
					+ "  collection_jitter = \"0s\"\r\n" + "  collection_offset = \"0s\"\r\n"
					+ "  name_override = \"http_url_check\"\r\n" + "  tags = {team = \"" + agentDto.getTeam() + "\", bu = \"" + agentDto.getBu() + "\"}\r\n"
					+ "\r\n");

			log.info("Service Url config details = {}", sb);
			String agentConfig = "";

			Optional<AgentDTO> agentDtoResult = agentConfigRepository.findById(101);
			if (agentDtoResult.isPresent()) {
				agentDto = agentDtoResult.get();
				agentConfig = agentDto.getAgentConfig() + "\r\n" + sb.toString();
				log.info("Agent config details in if condition = {}", agentConfig);
				agentDto.setAgentConfig(agentConfig);
				agentDtoList.add(agentDto);
			} else {
				agentConfig = KpadConstant.telegrafConfig + KpadConstant.urlConfig + sb.toString();
				log.info("Telegraf Agent config details in else condition = {}", agentConfig);
				agentDto.setAgentConfig(agentConfig);
				agentDto.setId(101);
				agentDto.setAgentConfigId(101);
				agentDtoList.add(agentDto);
			}
			agentDtoList = agentConfigRepository.saveAll(agentDtoList);
		} catch (Exception e) {
			log.error("End of updateTelegrafAgentConfig, Exception while calling updateTelegrafAgentConfig method  ", e);
			throw new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return agentDtoList;
	}
	
	@Override
	public List<ServiceUrlDTO> getServiceUrlList(String deptName) throws KpadException {
		List<ServiceUrlDTO> serviceUrlDTO = new ArrayList<>();
		try {
			log.info("Start of getServiceUrlList method");
			serviceUrlDTO = serviceUrlRepository.findAll();
			if (StringUtils.isNotBlank(deptName)) {
				serviceUrlDTO = serviceUrlDTO.stream().filter(ser -> ser.getDeptName().equalsIgnoreCase(deptName))
						.collect(Collectors.toList());
			}
		} catch (Exception e) {
			log.error("End of getServiceUrlList, Exception while calling getServiceUrlList method  ", e);
			throw new KpadException("400", "Exception while calling getServiceUrlList", HttpStatus.BAD_REQUEST);
		}
		return serviceUrlDTO;
	}

	private List<ServiceUrlDTO> insertServiceUrlDetails(AgentDTO agentDto) throws KpadException {
		List<ServiceUrlDTO> serviceUrlDTOList = new ArrayList<>();
		try {
			ServiceUrlDTO serviceUrlDTO = new ServiceUrlDTO();
			serviceUrlDTO.setServiceUrl(agentDto.getUpdatedServiceUrl());
			serviceUrlDTO.setAgentConfig(agentDto.getAgentConfig());
			serviceUrlDTO.setAgentConfigId(agentDto.getAgentConfigId());
			serviceUrlDTO.setDeptName(agentDto.getDeptName());
			serviceUrlDTO.setIsMaintenance(agentDto.getIsMaintenance());
			serviceUrlDTOList.add(serviceUrlDTO);
			log.info("Start of getServiceUrlList method");
			serviceUrlDTOList = serviceUrlRepository.saveAll(serviceUrlDTOList);
		} catch (Exception e) {
			log.error("End of getServiceUrlList, Exception while calling getServiceUrlList method  ", e);
			throw new KpadException("400", "Exception while calling getServiceUrlList", HttpStatus.BAD_REQUEST);
		}
		return serviceUrlDTOList;
	}

	@Override
	public List<FluxRecordDTO> getTelegrafInfluxRecord() throws KpadException {
		List<FluxRecordDTO> fluxRecordDTOList = new ArrayList<>();
		try {
			log.info("Start of getTelegrafInfluxRecord method");
//			String databaseURL = "http://localhost:8086";
//			String token = "OT83pAudBDhV6ssNLJ8xsKFlRF4ZkDvmOyi-lVKDbfZqIovMBltF-PpdVqOg2tAXWp63pt8NmshK-xfUKk-A9g==";
			char[] ch = new char[influxDbToken.length()];
			for (int i = 0; i < influxDbToken.length(); i++) {
				ch[i] = influxDbToken.charAt(i);
			}
			InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxDbUrl, ch, "TechQWare", "Kpad");
//			String flux = String.format("from(bucket: \"%s\")\n" + "      |> range(start: -24h)\n"
//					+ "      |> filter(fn: (r) => r[\"_measurement\"] == \"http_url_check\")\n"
//					+ "      |> filter(fn: (r) => r[\"_field\"] == \"result_code\")\n" + "      |> group()\n"
//					+ "      |> last()", "Kpad");
			
			String flux = String.format("from(bucket: \"%s\")\n" + "      |> range(start: -24h)\n"
					+ "      |> filter(fn: (r) => r[\"_measurement\"] == \"http_url_check\")\n"
					+ "      |> filter(fn: (r) => r[\"_field\"] == \"http_response_code\")\n" + "      |> group()\n", "Kpad");
			
			QueryApi queryApi = influxDBClient.getQueryApi();
			List<FluxTable> tables = queryApi.query(flux);
			for (FluxTable fluxTable : tables) {
				List<FluxRecord> records = fluxTable.getRecords();
				for (FluxRecord fluxRecord : records) {
					FluxRecordDTO fluxRecordDTO = new FluxRecordDTO();
					fluxRecordDTO.setTable(fluxRecord.getValueByKey("table") + "");
					fluxRecordDTO.setMeasurement(fluxRecord.getValueByKey("_measurement") + "");
					fluxRecordDTO.setField(fluxRecord.getValueByKey("_field") + "");
					fluxRecordDTO.setValue(fluxRecord.getValueByKey("_value") + "");
					fluxRecordDTO.setStartTime(fluxRecord.getValueByKey("_start") + "");
					fluxRecordDTO.setStopTime(fluxRecord.getValueByKey("_stop") + "");
					fluxRecordDTO.setTime(fluxRecord.getValueByKey("_time") + "");
					fluxRecordDTO.setBu(fluxRecord.getValueByKey("bu") + "");
					fluxRecordDTO.setHost(fluxRecord.getValueByKey("host") + "");
					fluxRecordDTO.setMethod(fluxRecord.getValueByKey("method") + "");
					fluxRecordDTO.setServer(fluxRecord.getValueByKey("server") + "");
					fluxRecordDTO.setStatusCode(fluxRecord.getValueByKey("status_code") + "");
					fluxRecordDTO.setTeam(fluxRecord.getValueByKey("team") + "");
					fluxRecordDTOList.add(fluxRecordDTO);
				}
			}
			log.info("fluxRecordDTOList = {} ", fluxRecordDTOList);
			influxDBClient.close();
		} catch (Exception e) {
			log.error("End of getTelegrafInfluxRecord, Exception while calling getTelegrafInfluxRecord method  ", e);
			throw new KpadException("400", "Exception while calling getTelegrafInfluxRecord", HttpStatus.BAD_REQUEST);
		}
		return fluxRecordDTOList;
	}

	@Override
	public String getAgentConfig() throws KpadException {
		String agentConfig = "";
		try {
			log.info("Start of updateAgentIndexConfig method");

			Optional<AgentDTO> agentDtoResult = agentConfigRepository.findById(101);
			AgentDTO agentDto = agentDtoResult.get();
			agentConfig = agentDto.getAgentConfig();
		} catch (Exception e) {
			log.error("End of updateAgentIndexConfig, Exception while calling updateAgentIndexConfig method  ", e);
			throw new KpadException("400", "Exception while calling updateAgentIndexConfig", HttpStatus.BAD_REQUEST);
		}
		return agentConfig;
	}
}
