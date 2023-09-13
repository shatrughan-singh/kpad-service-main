package com.kpad.app.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kpad.app.dto.AgentDTO;
import com.kpad.app.dto.DepartmentDTO;
import com.kpad.app.dto.FluxRecordDTO;
import com.kpad.app.dto.ServiceUrlDTO;
import com.kpad.app.service.KpadService;
import com.kpad.exceptions.KpadException;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class KpadController {

	@Autowired
	private KpadService kpadService;

	@GetMapping("/getDepartmentDetails")
	public ResponseEntity<Object> getDepartmentDetails(@RequestParam(value = "Id", required = false) String id) {

		System.out.println("This getResult Method");
		log.info("Start the getDepartmentDetails method dept ID", id);
		List<DepartmentDTO> departmentDTO = new ArrayList<>();
		try {
			departmentDTO = kpadService.getDepartmentDetails();
		} catch (Exception e) {
			log.error("Exception in the getDepartmentDetails method dept ID", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(departmentDTO, HttpStatus.OK);
	}

	@GetMapping("/getDepartmentCountDetails")
	public ResponseEntity<Object> getDepartmentCountDetails(
			@RequestParam(value = "deptName", required = false) String deptName) {
		log.info("Start the getDepartmentCountDetails method dept Name = {}", deptName);
		List<DepartmentDTO> departmentDTO = new ArrayList<>();
		try {
			departmentDTO = kpadService.getDepartmentCountDetails(deptName);
		} catch (Exception e) {
			log.error("Exception in the getDepartmentCountDetails method", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(departmentDTO, HttpStatus.OK);
	}

	@GetMapping("/getServiceUrlList")
	public ResponseEntity<Object> getServiceUrlList(
			@RequestParam(value = "deptName", required = false) String deptName) {
		log.info("Start the getServiceUrlList method dept Name = {}", deptName);
		List<ServiceUrlDTO> serviceUrlDTOList = new ArrayList<>();
		try {
			serviceUrlDTOList = kpadService.getServiceUrlList(deptName);
		} catch (Exception e) {
			log.error("Exception in the getServiceUrlList method dept ID", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(serviceUrlDTOList, HttpStatus.OK);
	}

	@GetMapping("/getDepartmentDetailsFromInfluxDB")
	public ResponseEntity<Object> getDepartmentDetailsFromInfluxDB(
			@RequestParam(value = "server", required = false) String server) {
		log.info("Start the getDepartmentDetailsFromInfluxDB method dept ID = {}", server);
		Integer onlineCount = 0;
		try {
			onlineCount = kpadService.getDepartmentDetailsFromInfluxDB(server);
		} catch (Exception e) {
			log.error("Exception in the getDepartmentDetailsFromInfluxDB method dept ID", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(onlineCount, HttpStatus.OK);
	}

	@PostMapping("/saveDepartmentDetails")
	public ResponseEntity<Object> saveDepartmentDetails(@RequestBody List<DepartmentDTO> deptDtoList) {
		log.info("Start the saveDepartmentDetails method deptDtoList = {}", deptDtoList);
		List<DepartmentDTO> departmentDTO = new ArrayList<>();
		try {
			departmentDTO = kpadService.saveDepartmentDetails(deptDtoList);
		} catch (Exception e) {
			log.error("Exception in the saveDepartmentDetails method dept ID", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(departmentDTO, HttpStatus.OK);
	}

	@PostMapping("/updateTelegrafAgentConfig")
	public ResponseEntity<Object> updateTelegrafAgentConfig(@RequestBody AgentDTO agentDto) {
		log.info("Start the updateTelegrafAgentConfig method agentDto = {}", agentDto);
		List<AgentDTO> departmentDTO = new ArrayList<>();
		try {
			departmentDTO = kpadService.updateTelegrafAgentConfig(agentDto);
		} catch (Exception e) {
			log.error("Exception in the updateTelegrafAgentConfig method", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(departmentDTO, HttpStatus.OK);
	}

	@GetMapping("/getTelegrafInfluxRecord")
	public ResponseEntity<Object> getTelegrafInfluxRecord() {
		log.info("Start the getTelegrafInfluxRecord method  = {}");
		List<FluxRecordDTO> fluxRecordDTO = new ArrayList<>();
		try {
			fluxRecordDTO = kpadService.getTelegrafInfluxRecord();
		} catch (Exception e) {
			log.error("Exception in the getTelegrafInfluxRecord method ", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(fluxRecordDTO, HttpStatus.OK);
	}

	@GetMapping("/getTelegrafAgentConfig")
	public ResponseEntity<Object> getTelegrafAgentConfig() {
		log.info("Start the getTelegrafAgentConfig method agentDto = {}");
		String agentConfig = "";
		try {
			agentConfig = kpadService.getAgentConfig();
		} catch (Exception e) {
			log.error("Exception in the getTelegrafAgentConfig method dept ID", e);
			KpadException kpadException = new KpadException("400", e.getMessage(), HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(kpadException, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(agentConfig, HttpStatus.OK);
	}
}
