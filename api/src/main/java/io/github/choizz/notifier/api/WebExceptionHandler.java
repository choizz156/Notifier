package io.github.choizz.notifier.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.choizz.logging.LogContent;
import io.github.choizz.notifier.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {

		log.warn("[EXCEPTION] IllegalArgumentException 예외 응답 반환", LogContent.exception(e));
		return ResponseEntity.badRequest().body(ErrorResponse.from(e));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		log.warn("[EXCEPTION] MethodArgumentNotValidException 예외 응답 반환", LogContent.exception(e));
		return ResponseEntity.badRequest()
			.body(ErrorResponse.from(e.getBindingResult()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {

		log.error("[EXCEPTION] 핸들링 하지 못한 Exception 예외 응답 반환", LogContent.exception(e));
		return ResponseEntity.internalServerError().body(ErrorResponse.from(e));
	}
}
