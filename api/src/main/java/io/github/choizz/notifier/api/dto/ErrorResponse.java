package io.github.choizz.notifier.api.dto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

public record ErrorResponse(
	String message,
	String timestamp
) {

	public static ErrorResponse from(Exception exception) {

		return new ErrorResponse(
			exception.getMessage(),
			LocalDateTime.now().toString());
	}

	public static ErrorResponse from(BindingResult bindingResult) {

		String errorMessage = bindingResult.getFieldErrors().stream()
			.map(error -> error.getDefaultMessage())
			.collect(Collectors.joining(", "));
		return new ErrorResponse(errorMessage, LocalDateTime.now().toString());
	}
}
