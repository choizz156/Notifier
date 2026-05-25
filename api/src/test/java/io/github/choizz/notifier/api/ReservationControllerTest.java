package io.github.choizz.notifier.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.choizz.notifier.api.dto.ReservationCreateWebRequest;
import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;
import io.github.choizz.notifier.core.domain.model.NotificationType;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReservationUseCase reservationUseCase;

	@DisplayName("알림 예약 API를 호출하면 202 ACCEPTED를 반환한다.")
	@Test
	void test1() throws Exception {
		// given
		String requestJson = "{\"subscriberIds\": [1, 2], \"type\": \"PAYMENT_CONFIRMED\", \"reservationTime\": \"2026-01-01T10:00:00\"}";
		
		doNothing().when(reservationUseCase).reserve(any(), any(), any());

		// when & then
		mockMvc.perform(post("/v1/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isAccepted());
	}
}
