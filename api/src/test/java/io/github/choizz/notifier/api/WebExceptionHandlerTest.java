package io.github.choizz.notifier.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import io.github.choizz.notifier.api.dto.ErrorResponse;

class WebExceptionHandlerTest {

	private final WebExceptionHandler exceptionHandler = new WebExceptionHandler();

	@DisplayName("NoSuchElementException 발생 시 404 NOT_FOUND를 반환한다.")
	@Test
	void test1() {
		// given
		NoSuchElementException exception = new NoSuchElementException("Not Found");

		// when
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleEntityNotFoundException(exception);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Not Found");
	}

	@DisplayName("IllegalStateException 발생 시 409 CONFLICT를 반환한다.")
	@Test
	void test2() {
		// given
		IllegalStateException exception = new IllegalStateException("Conflict");

		// when
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalStateException(exception);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Conflict");
	}

	@DisplayName("IllegalArgumentException 발생 시 400 BAD_REQUEST를 반환한다.")
	@Test
	void test3() {
		// given
		IllegalArgumentException exception = new IllegalArgumentException("Bad Request");

		// when
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Bad Request");
	}

	@DisplayName("MethodArgumentNotValidException 발생 시 400 BAD_REQUEST와 검증 에러 메시지를 반환한다.")
	@Test
	void test4() {
		// given
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("object", "field", "Invalid field");
		when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
		
		MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
		when(exception.getBindingResult()).thenReturn(bindingResult);
		when(exception.getMessage()).thenReturn("Validation failed");

		// when
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(exception);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Invalid field");
	}

	@DisplayName("처리되지 않은 Exception 발생 시 500 INTERNAL_SERVER_ERROR를 반환한다.")
	@Test
	void test5() {
		// given
		Exception exception = new Exception("Server Error");

		// when
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Server Error");
	}
}
