package io.github.choizz.notifier.core.application.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

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

@ExtendWith(MockitoExtension.class)
class NotificationFailedEventHandlerTest {

	@Mock
	private NotificationUseCase notificationUseCase;

	@Mock
	private NotificationLogUseCase notificationLogUseCase;

	@InjectMocks
	private NotificationFailedEventHandler handler;

	@DisplayName("알림 발송 실패 이벤트가 발생하면 FAILED 로그를 저장하고 알림 상태를 실패로 변경한다.")
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
			.build();
		PublishFailedEvent event = new PublishFailedEvent(context);

		// when
		handler.updateNotification(event);

		// then
		verify(notificationLogUseCase, times(1)).savenotificationLog(eq(1L), eq(EventStatus.FAILED), eq(context));
		verify(notificationUseCase, times(1)).fail(1L, "connection error");
	}
}
