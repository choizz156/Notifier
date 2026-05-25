package io.github.choizz.notifier.core.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.dto.NotificationDetailResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.application.port.out.LoadCombinedNotificationPort;
import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.application.support.NotificationRetryProcessor;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@Mock
	private NotificationPersistencePort notificationPersistencePort;
	@Mock
	private NotificationLogUseCase notificationLogUseCase;
	@Mock
	private TemplateRendererPort templateRendererPort;
	@Mock
	private NotificationRetryProcessor notificationRetryProcessor;
	@Mock
	private MockUserPersistencePort mockUserPersistencePort;
	@Mock
	private LoadCombinedNotificationPort loadCombinedNotificationPort;

	@InjectMocks
	private NotificationService notificationService;

	@DisplayName("알림 수신을 거부한 유저에게는 알림을 발송하지 않는다.")
	@Test
	void test1() {
		// given
		NotificationContext context = new NotificationContext(1L, "PAYMENT_CONFIRMED", Map.of());
		when(mockUserPersistencePort.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED)).thenReturn(false);

		// when
		notificationService.push(context);

		// then
		verify(mockUserPersistencePort, never()).findSubscribedChannels(any());
		verify(notificationPersistencePort, never()).saveAll(any());
	}

	@DisplayName("활성화된 채널이 없으면 알림을 발송하지 않는다.")
	@Test
	void test2() {
		// given
		NotificationContext context = new NotificationContext(1L, "PAYMENT_CONFIRMED", Map.of());
		when(mockUserPersistencePort.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED)).thenReturn(true);
		when(mockUserPersistencePort.findSubscribedChannels(1L)).thenReturn(Set.of());

		// when
		notificationService.push(context);

		// then
		verify(notificationPersistencePort, never()).saveAll(any());
	}

	@DisplayName("활성화된 채널이 있고 수신 동의했다면 알림을 저장하고 이벤트를 발행한다.")
	@Test
	void test3() {
		// given
		NotificationContext context = new NotificationContext(1L, "PAYMENT_CONFIRMED", Map.of());
		when(mockUserPersistencePort.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED)).thenReturn(true);
		when(mockUserPersistencePort.findSubscribedChannels(1L)).thenReturn(Set.of(Channel.EMAIL));
		when(notificationPersistencePort.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL)).thenReturn(false);
		
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.status(NotificationStatus.PENDING)
			.build();
		when(notificationPersistencePort.saveAll(any())).thenReturn(List.of(notification));

		// when
		notificationService.push(context);

		// then
		verify(notificationPersistencePort, times(1)).saveAll(any());
		verify(applicationEventPublisher, times(1)).publishEvent(any(NotificationRequestedEvent.class));
	}

	@DisplayName("중복 알림이면 발송을 무시한다.")
	@Test
	void test4() {
		// given
		NotificationContext context = new NotificationContext(1L, "PAYMENT_CONFIRMED", Map.of());
		when(mockUserPersistencePort.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED)).thenReturn(true);
		when(mockUserPersistencePort.findSubscribedChannels(1L)).thenReturn(Set.of(Channel.EMAIL));
		when(notificationPersistencePort.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL)).thenReturn(true);

		// when
		notificationService.push(context);

		// then
		verify(notificationPersistencePort, never()).saveAll(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
	}

	@DisplayName("알림 상태를 업데이트한다.")
	@Test
	void test5() {
		// given
		Notification notification = Notification.builder().status(NotificationStatus.PENDING).build();
		when(notificationPersistencePort.findById(1L)).thenReturn(notification);

		// when
		notificationService.updateStatus(1L, NotificationStatus.COMPLETED);

		// then
		assertThat(notification.status()).isEqualTo(NotificationStatus.COMPLETED);
		verify(notificationPersistencePort, times(1)).save(notification);
	}

	@DisplayName("알림을 실패 처리한다.")
	@Test
	void test6() {
		// given
		Notification notification = Notification.builder().status(NotificationStatus.PENDING).build();
		when(notificationPersistencePort.findById(1L)).thenReturn(notification);

		// when
		notificationService.fail(1L, "reason");

		// then
		assertThat(notification.status()).isEqualTo(NotificationStatus.FAILED);
		assertThat(notification.failMessage()).isEqualTo("reason");
		verify(notificationPersistencePort, times(1)).save(notification);
	}

	@DisplayName("알림 상세를 조회한다.")
	@Test
	void test7() {
		// given
		Notification notification = Notification.builder()
			.id(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.status(NotificationStatus.COMPLETED)
			.build();
		when(notificationPersistencePort.findById(1L)).thenReturn(notification);
		when(templateRendererPort.render(Channel.EMAIL, NotificationType.PAYMENT_CONFIRMED, Map.of())).thenReturn("rendered content");

		// when
		NotificationDetailResponse response = notificationService.findNotificationDetail(1L);

		// then
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.content()).isEqualTo("rendered content");
	}

	@DisplayName("알림 상태를 조회한다.")
	@Test
	void test8() {
		// given
		Notification notification = Notification.builder()
			.id(1L)
			.status(NotificationStatus.FAILED)
			.build();
		when(notificationPersistencePort.findById(1L)).thenReturn(notification);

		// when
		NotificationStatusResponse response = notificationService.findStatus(1L);

		// then
		assertThat(response.notificationId()).isEqualTo(1L);
		assertThat(response.status()).isEqualTo(NotificationStatus.FAILED);
	}

	@DisplayName("알림을 읽음 처리한다.")
	@Test
	void test9() {
		// given
		Long notificationId = 1L;

		// when
		notificationService.markAsRead(notificationId);

		// then
		verify(notificationPersistencePort, times(1)).markAsRead(notificationId);
	}

	@DisplayName("알림 목록을 조회한다.")
	@Test
	void test10() {
		// given
		Long subscriberId = 1L;
		Boolean isRead = false;
		int page = 0;
		int size = 10;
		io.github.choizz.notifier.core.application.dto.PageResult<io.github.choizz.notifier.core.application.dto.NotificationResponse> expectedResult = new io.github.choizz.notifier.core.application.dto.PageResult<>(List.of(), 0, 0, 0L, 0);
		when(loadCombinedNotificationPort.loadCombinedNotifications(subscriberId, isRead, page, size)).thenReturn(expectedResult);

		// when
		io.github.choizz.notifier.core.application.dto.PageResult<io.github.choizz.notifier.core.application.dto.NotificationResponse> result = notificationService.findNotifications(subscriberId, isRead, page, size);

		// then
		assertThat(result).isEqualTo(expectedResult);
		verify(loadCombinedNotificationPort, times(1)).loadCombinedNotifications(subscriberId, isRead, page, size);
	}

	@DisplayName("단건 재시도를 수행한다.")
	@Test
	void test11() {
		// given
		List<Long> notificationIds = List.of(1L, 2L);

		// when
		notificationService.retryStuckNotification(notificationIds);

		// then
		verify(notificationRetryProcessor, times(1)).processChunk(notificationIds);
	}

	@DisplayName("전체 실패 건 재시도를 수행한다.")
	@Test
	void test12() {
		// given
		when(notificationLogUseCase.findUnprocessedNotificationIds(0L, 500))
			.thenReturn(List.of(1L, 2L, 3L));

		// when
		notificationService.retry();

		// then
		verify(notificationLogUseCase, times(1)).findUnprocessedNotificationIds(any(Long.class), any(Integer.class));
		verify(notificationRetryProcessor, times(1)).processChunk(List.of(1L, 2L, 3L));
	}
}
