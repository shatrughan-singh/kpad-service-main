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
public class ServiceUrlDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    private String id;

	private String agentConfig;

	private String serviceUrl;
	
	private Integer agentConfigId;
	
	private String deptName;
	
	private Boolean isMaintenance;

}
