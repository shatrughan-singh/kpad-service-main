package com.kpad.app.service;

import java.util.List;
import java.util.Map;

import com.kpad.app.dto.AgentDTO;
import com.kpad.app.dto.DepartmentDTO;
import com.kpad.app.dto.FluxRecordDTO;
import com.kpad.app.dto.ServiceUrlDTO;
import com.kpad.exceptions.KpadException;

public interface KpadService {

	public List<DepartmentDTO> getDepartmentDetails() throws KpadException;

	public List<DepartmentDTO> saveDepartmentDetails(List<DepartmentDTO> deptDtoList) throws KpadException;
	
	public List<DepartmentDTO> agentUpdateConfig(List<DepartmentDTO> deptDtoList) throws KpadException;

	public List<AgentDTO> updateTelegrafAgentConfig(AgentDTO agentDto) throws KpadException;

	public List<FluxRecordDTO> getTelegrafInfluxRecord() throws KpadException;

	public String getAgentConfig() throws KpadException;

	public Integer getDepartmentDetailsFromInfluxDB(String server) throws KpadException;

	public List<DepartmentDTO> getDepartmentCountDetails(String deptName) throws KpadException;

	public List<ServiceUrlDTO> getServiceUrlList(String deptName) throws KpadException;

}
