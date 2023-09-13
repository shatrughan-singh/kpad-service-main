package com.kpad.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(KpadException.class)
	public ResponseEntity<GlobalException> ReportGeneratorExceptionHandler(
			KpadException reportGeneratorException) {
		ResponseEntity<GlobalException> responseEntity;
		GlobalException globalError = new GlobalException(reportGeneratorException.getErrorCode(),
				reportGeneratorException.getErrorMessage(), reportGeneratorException.getStatusCode());
		responseEntity = new ResponseEntity<>(globalError, globalError.getStatusCode());
		return responseEntity;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalException> exceptionHandler(HttpServletRequest req, Exception e) {
		return new ResponseEntity<>(
				new GlobalException("1000", "Unknown exception occurred.", HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}