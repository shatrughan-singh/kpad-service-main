package com.kpad.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class GlobalException {

	private String errorCode;
	private String errorMessage;
	private HttpStatus statusCode;

	public GlobalException(String errorCode, String errorMessage, HttpStatus statusCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.statusCode = statusCode;
	}
}
