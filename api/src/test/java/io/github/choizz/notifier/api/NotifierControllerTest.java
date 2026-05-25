package io.github.choizz.notifier.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.choizz.notifier.api.dto.NotificationCreateWebRequest;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;

@WebMvcTest(NotifierController.class)
class NotifierControllerTest {

	@Autowired
	private MockMvc mockMvc;



	@MockitoBean
	private NotificationUseCase notificationUseCase;

	@DisplayName("알림 이벤트 발행 API 시나리오 테스트")
	@TestFactory
	List<DynamicTest> test1() {
		return List.of(
			DynamicTest.dynamicTest("정상적으로 알림 이벤트를 발행하면 202 ACCEPTED를 반환한다.", () -> {
				// given
				String requestJson = "{\"subscriberId\": 1, \"notificationType\": \"PAYMENT_CONFIRMED\", \"metadata\": {\"message\": \"테스트 알림입니다.\"}}";
				
				doNothing().when(notificationUseCase).push(any());

				// when & then
				mockMvc.perform(post("/v1/Notification")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
					.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
					.andExpect(status().isAccepted());
			}),
			
			DynamicTest.dynamicTest("필수 파라미터가 누락된 경우를 테스트한다.", () -> {
				// given
				String requestJson = "{\"subscriberId\": null, \"NotificationType\": null, \"metadata\": null}";

				// when & then
				mockMvc.perform(post("/v1/Notification")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
					.andExpect(status().isBadRequest());
			})
		);
	}
}
