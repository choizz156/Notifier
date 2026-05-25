package io.github.choizz.notifier.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

	@DisplayName("공통 알림을 읽음 처리하는 API를 호출한다.")
	@Test
	void markAsRead() throws Exception {
		// given
		Long publicNotificationId = 100L;
		Long subscriberId = 1L;

		// when & then
		mockMvc.perform(
				patch("/v1/public/notification/{id}/read", publicNotificationId)
					.param("subscriberId", String.valueOf(subscriberId))
			)
			.andExpect(status().isOk());

		verify(publicNotificationUseCase, times(1)).markAsRead(subscriberId, publicNotificationId);
	}
}
