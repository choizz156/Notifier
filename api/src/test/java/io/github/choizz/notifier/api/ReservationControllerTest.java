package io.github.choizz.notifier.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ReservationUseCase reservationUseCase;

	@InjectMocks
	private ReservationController reservationController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
			.setControllerAdvice(new WebExceptionHandler())
			.build();
	}

	@DisplayName("공개 알림 예약 API를 호출하면 202 ACCEPTED를 반환한다.")
	@Test
	void test1() throws Exception {
		// given
		String requestJson = "{\"type\": \"NEW_LECTURE_OPENED\", \"metadata\": {\"key\": \"value\"}, \"reservationTime\": \"2026-01-01T10:00:00\"}";
		
		doNothing().when(reservationUseCase).reservePublic(any(), any(), any());

		// when & then
		mockMvc.perform(post("/v1/reservations/public")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isAccepted());
	}
}
