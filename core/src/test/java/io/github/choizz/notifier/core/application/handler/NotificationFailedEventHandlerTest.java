package io.github.choizz.notifier.core.application.handler;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

@ExtendWith(MockitoExtension.class)
class NotificationFailedEventHandlerTest {

	@Mock
	private NotificationUseCase notificationUseCase;

	@Mock
	private NotificationLogUseCase notificationLogUseCase;

	@InjectMocks
	private NotificationFailedEventHandler handler;

	@DisplayName("개인 알림 발송 실패 이벤트가 발생하면 FAILED 로그를 저장하고 알림 상태를 실패로 변경한다.")
	@Test
	void test1() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("connection error")
			.metadata("{}")
			.retryCount(3)
			.referenceType(ReferenceType.PERSONAL.name())
			.build();
		PublishFailedEvent event = new PublishFailedEvent(context);

		// when
		handler.updateNotification(event);

		// then
		verify(notificationLogUseCase, times(1)).saveNotificationLog(eq(1L), eq(EventStatus.FAILED), eq(context));
		verify(notificationUseCase, times(1)).fail(1L, "connection error");
	}

	@DisplayName("공통 알림 발송 실패 이벤트가 발생하면 FAILED 로그만 저장하고 알림 상태 변경은 호출하지 않는다.")
	@Test
	void test2() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("connection error")
			.metadata("{}")
			.retryCount(3)
			.referenceType(ReferenceType.PUBLIC.name())
			.build();
		PublishFailedEvent event = new PublishFailedEvent(context);

		// when
		handler.updateNotification(event);

		// then
		verify(notificationLogUseCase, times(1)).saveNotificationLog(eq(2L), eq(EventStatus.FAILED), eq(context));
		verify(notificationUseCase, times(0)).fail(2L, "connection error");
	}
}
