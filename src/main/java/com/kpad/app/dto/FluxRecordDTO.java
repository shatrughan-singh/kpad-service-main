package com.kpad.app.dto;

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
public class FluxRecordDTO {

	private String table;
	
	private String measurement;
	
	private String field;
	
	private String value;
	
	private String startTime;
	
	private String stopTime;
	
	private String time;
	
	private String bu;
	
	private String host;
	
	private String method;
	
	private String server;
	
	private String statusCode;
	
	private String team;
}
