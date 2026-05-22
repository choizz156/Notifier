package io.github.choizz.notifier.api;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
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

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(NoSuchElementException e) {
		log.warn("[EXCEPTION] NoSuchElementException 예외 응답 반환", LogContent.exception(e));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(e));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
		log.warn("[EXCEPTION] IllegalStateException() 예외 응답 반환", LogContent.exception(e));
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.from(e));
	}

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
