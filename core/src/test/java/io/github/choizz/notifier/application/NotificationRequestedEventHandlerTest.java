package io.github.choizz.notifier.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.application.dto.NotificationContext;
import io.github.choizz.notifier.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.notifier.domain.model.Notification;
import io.github.choizz.notifier.domain.model.NotificationEventLog;
import io.github.choizz.notifier.domain.model.NotificationType;

@Disabled
@ExtendWith(MockitoExtension.class)
class NotificationRequestedEventHandlerTest {

	@Mock
	private NotificationEventLogPersistencePort eventLogPersistencePort;

	@Mock
	private NotifierFacade notifierFacade;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private NotificationRequestedEventHandler sut;

	@Test
	@DisplayName("알림 요청 이벤트가 발생하면 REQUESTED 상태의 이벤트 로그를 저장한다.")
	void test1() {
		// given
		NotificationContext context = new NotificationContext(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of());
		Notification notification = Notification.builder().id(10L).subscriberId(context.subscriberId()).notificationType(context.notificationType()).channel(context.channel()).metadata("{}").build();
		NotificationRequestedEvent event = NotificationRequestedEvent.of(notification, context);

		// when
		sut.saveEvent(event);

		// then
		verify(eventLogPersistencePort).save(any(NotificationEventLog.class));
	}

	@Test
	@DisplayName("트랜잭션 커밋 후 비동기로 실제 알림 발송 처리를 수행한다.")
	void test2() {
		// given
		NotificationContext context = new NotificationContext(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of());
		Notification notification = Notification.builder().id(10L).subscriberId(context.subscriberId()).notificationType(context.notificationType()).channel(context.channel()).metadata("{}").build();
		NotificationRequestedEvent event = NotificationRequestedEvent.of(notification, context);

		// when
		sut.publishNotification(event);

		// then
		verify(notifierFacade).publish(event);
	}

	@Test
	@DisplayName("알림 발송에 실패하면 실패 로그를 저장하고 재시도 정책에 따라 이벤트를 다시 발행한다.")
	void test3() {
		// given
		NotificationContext context = new NotificationContext(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of());
		Notification notification = Notification.builder().id(10L).subscriberId(context.subscriberId()).notificationType(context.notificationType()).channel(context.channel()).metadata("{}").build();
		NotificationRequestedEvent event = NotificationRequestedEvent.of(notification, context);

		doThrow(new RuntimeException("네트워크 오류")).when(notifierFacade).publish(event);
		given(eventLogPersistencePort.findLatestByNotificationId(event.notificationId())).willReturn(null);
		// when
		sut.publishNotification(event);

		// then
		verify(eventLogPersistencePort).save(any(NotificationEventLog.class));
		verify(eventPublisher).publishEvent(event);
	}
}
