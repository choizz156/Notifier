package io.github.choizz.notifier.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.choizz.notifier.core.application.dto.NotificationDetailResponse;
import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotifierControllerTest {

	private MockMvc mockMvc;

	@Mock
	private NotificationUseCase notificationUseCase;

	@InjectMocks
	private NotifierController notifierController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(notifierController)
			.setControllerAdvice(new WebExceptionHandler())
			.build();
	}

	@DisplayName("알림 이벤트 발행 API 시나리오 테스트")
	@TestFactory
	List<DynamicTest> test1() {
		return List.of(
			DynamicTest.dynamicTest("정상적으로 알림 이벤트를 발행하면 202 ACCEPTED를 반환한다.", () -> {
				// given
				String requestJson = "{\"subscriberId\": 1, \"notificationType\": \"PAYMENT_CONFIRMED\", \"metadata\": {\"message\": \"테스트 알림입니다.\"}}";

				// when & then
				mockMvc.perform(post("/v1/notification")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
					.andDo(MockMvcResultHandlers.print())
					.andExpect(status().isAccepted());
			}),
			
			DynamicTest.dynamicTest("필수 파라미터가 누락된 경우를 테스트한다.", () -> {
				// given
				String requestJson = "{\"subscriberId\": null, \"NotificationType\": null, \"metadata\": null}";

				// when & then
				mockMvc.perform(post("/v1/notification")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
					.andExpect(status().isBadRequest());
			})
		);
	}

	@DisplayName("알림 상태 조회 API를 호출하면 200 OK와 상태 정보를 반환한다.")
	@Test
	void test2() throws Exception {
		// given
		Long notificationId = 1L;
		NotificationStatusResponse response = new NotificationStatusResponse(notificationId, NotificationStatus.PENDING);

		when(notificationUseCase.findStatus(notificationId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/v1/notification/{id}/status", notificationId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.notificationId").value(notificationId))
			.andExpect(jsonPath("$.status").value("PENDING"));

		verify(notificationUseCase, times(1)).findStatus(notificationId);
	}

	@DisplayName("구독자별 알림 목록 조회 API를 호출하면 200 OK와 페이징된 결과를 반환한다.")
	@Test
	void test3() throws Exception {
		// given
		Long subscriberId = 1L;
		NotificationResponse notificationResponse = new NotificationResponse(
			1L, null, subscriberId, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP,
			NotificationStatus.COMPLETED, "결제 완료", true, LocalDateTime.now(), 0
		);
		PageResult<NotificationResponse> pageResult = new PageResult<>(
			List.of(notificationResponse), 0, 20, 1L, 1
		);

		when(notificationUseCase.findNotifications(eq(subscriberId), any(), eq(0), eq(20)))
			.thenReturn(pageResult);

		// when & then
		mockMvc.perform(get("/v1/notification/subscribers/{subscriberId}", subscriberId)
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.totalElements").value(1));

		verify(notificationUseCase, times(1)).findNotifications(eq(subscriberId), any(), eq(0), eq(20));
	}

	@DisplayName("알림 상세 조회 API를 호출하면 200 OK와 상세 정보를 반환한다.")
	@Test
	void test4() throws Exception {
		// given
		Long notificationId = 1L;
		NotificationDetailResponse detailResponse = new NotificationDetailResponse(
			notificationId, 1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP,
			NotificationStatus.COMPLETED, "결제 완료", "결제가 완료되었습니다.", true, LocalDateTime.now(), 0
		);

		when(notificationUseCase.findNotificationDetail(notificationId)).thenReturn(detailResponse);

		// when & then
		mockMvc.perform(get("/v1/notification/{id}", notificationId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(notificationId))
			.andExpect(jsonPath("$.content").value("결제가 완료되었습니다."));

		verify(notificationUseCase, times(1)).findNotificationDetail(notificationId);
	}

	@DisplayName("앱에서 알림 읽음 처리 API를 호출하면 204 NO_CONTENT를 반환한다.")
	@Test
	void test5() throws Exception {
		// given
		Long notificationId = 1L;

		doNothing().when(notificationUseCase).markAsRead(notificationId);

		// when & then
		mockMvc.perform(patch("/v1/notification/{id}/read", notificationId))
			.andExpect(status().isNoContent());

		verify(notificationUseCase, times(1)).markAsRead(notificationId);
	}

	@DisplayName("이메일에서 알림 읽음 처리 API를 호출하면 302 리다이렉트를 반환한다.")
	@Test
	void test6() throws Exception {
		// given
		Long notificationId = 1L;

		doNothing().when(notificationUseCase).markAsRead(notificationId);

		// when & then
		mockMvc.perform(get("/v1/notification/{id}/read", notificationId))
			.andExpect(status().isFound())
			.andExpect(header().string("Location", "http://localhost:8080/"));

		verify(notificationUseCase, times(1)).markAsRead(notificationId);
	}

	@DisplayName("알림 재시도 API를 호출하면 202 ACCEPTED를 반환한다.")
	@Test
	void test7() throws Exception {
		// given
		doNothing().when(notificationUseCase).retry();

		// when & then
		mockMvc.perform(post("/v1/notification/retry"))
			.andExpect(status().isAccepted());

		verify(notificationUseCase, times(1)).retry();
	}
}

