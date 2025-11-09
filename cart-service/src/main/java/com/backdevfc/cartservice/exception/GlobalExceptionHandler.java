package com.backdevfc.cartservice.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleException(Exception ex) {
		log.error("Exception: {}", ex.getMessage(), ex);

		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		problem.setTitle("Internal Server Error");
		problem.setDetail(ex.getMessage());
		problem.setProperty("timestamp", getFormatted());

		return problem;
	}


	@ExceptionHandler(HttpClientErrorException.class)
	public ProblemDetail handleHttpClientError(HttpClientErrorException ex) {
		log.error("HttpClientErrorException: {}", ex.getMessage(), ex);

		ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
		problem.setTitle("Client Error");
		problem.setDetail(ex.getMessage());
		problem.setProperty("timestamp", getFormatted());

		return problem;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
		log.error("ResourceNotFoundException: {}", ex.getMessage(), ex);

		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
		problem.setTitle("Resource Not Found");
		problem.setDetail(ex.getMessage());
		problem.setProperty("timestamp", getFormatted());

		return problem;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {

		log.error("Validation error: {}", ex.getMessage(), ex);

		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problem.setTitle("Validation Failed");
		problem.setDetail("One or more fields are invalid");
		problem.setProperty("timestamp", getFormatted());

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String field = toSnakeCase(error.getField());
			errors.put(field, error.getDefaultMessage());
		});

		problem.setProperty("errors", errors);

		return ResponseEntity.badRequest().body(problem);
	}

	private static String getFormatted() {
		return LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	private String toSnakeCase(String input) {
		return input
				.replaceAll("([a-z])([A-Z])", "$1_$2")
				.toLowerCase();
	}
}