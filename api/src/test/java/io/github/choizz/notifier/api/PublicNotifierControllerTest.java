package io.github.choizz.notifier.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;

@ExtendWith(MockitoExtension.class)
class PublicNotifierControllerTest {

	private MockMvc mockMvc;

	@Mock
	private PublicNotificationUseCase publicNotificationUseCase;

	@InjectMocks
	private PublicNotifierController publicNotifierController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(publicNotifierController)
			.setControllerAdvice(new WebExceptionHandler())
			.build();
	}

	@DisplayName("공통 알림 발송 API를 호출하면 202 ACCEPTED를 반환한다.")
	@Test
	void test1() throws Exception {
		// given
		String requestJson = "{\"type\": \"PAYMENT_CONFIRMED\", \"metadata\": {\"message\": \"공통 알림 테스트\"}}";

		doNothing().when(publicNotificationUseCase).pushToPublic(any());

		// when & then
		mockMvc.perform(post("/v1/notifications/public")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isAccepted());

		verify(publicNotificationUseCase, times(1)).pushToPublic(any());
	}

	@DisplayName("공통 알림을 읽음 처리하는 API를 호출한다.")
	@Test
	void test2() throws Exception {
		// given
		Long publicNotificationId = 100L;
		Long subscriberId = 1L;

		// when & then
		mockMvc.perform(
				patch("/v1/notifications/public/{id}/read", publicNotificationId)
					.param("subscriberId", String.valueOf(subscriberId))
			)
			.andExpect(status().isOk());

		verify(publicNotificationUseCase, times(1)).markAsRead(subscriberId, publicNotificationId);
	}

	@DisplayName("이메일에서 홈페이지로 이동 시 공통 알림을 읽음 처리하고 리다이렉트한다.")
	@Test
	void test3() throws Exception {
		// given
		Long publicNotificationId = 100L;
		Long subscriberId = 1L;

		// when & then
		mockMvc.perform(
				org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/notifications/public/{id}/read", publicNotificationId)
					.param("subscriberId", String.valueOf(subscriberId))
			)
			.andExpect(status().isFound())
			.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Location", "http://localhost:8080/"));

		verify(publicNotificationUseCase, times(1)).markAsRead(subscriberId, publicNotificationId);
	}
}
