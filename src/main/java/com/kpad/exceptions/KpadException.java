package com.kpad.exceptions;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class KpadException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMessage;
	private HttpStatus statusCode;

	public KpadException(GlobalException globalError) {
		super(globalError.getErrorCode() + "-" + globalError.getErrorMessage());

	}

	public KpadException(String errorCode, String errorMessage, HttpStatus statusCode) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.statusCode = statusCode;

	}

}
