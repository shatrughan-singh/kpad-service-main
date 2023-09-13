package com.kpad.app.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode()
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class AgentDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    private Integer id;

	private String agentConfig;

	private String updatedServiceUrl;
	
	private Integer agentConfigId;
	
	private String bu;
	
	private String team;
	
	private String interval;
	
	private String responseTimeout;
	
	private String deptId;
	
	private String deptName;
	
	private Boolean isMaintenance;

}
