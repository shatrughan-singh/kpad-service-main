package com.kpad.app.dto;

import java.io.Serializable;

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
public class DepartmentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String deptId;

	private String deptName;
	
	private Integer onlineCount;
	
	private Integer offlineCount;
	
	private Integer maintanceCount;
	
	private Integer totalDeptServiceCount;

	private Integer totalOnlineCount;
	
	private Integer totalOfflineCount;
	
	private Integer totalMaintenanceCount;
}
