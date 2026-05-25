package io.github.choizz.notifier.core.application.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

@ExtendWith(MockitoExtension.class)
class NotificationRequestedEventHandlerTest {

	@Mock
	private NotificationLogPersistencePort notificationLogPersistencePort;

	@Mock
	private NotificationEventPublisher notificationEventPublisher;

	@InjectMocks
	private NotificationRequestedEventHandler handler;

	@DisplayName("알림 요청 이벤트가 발생하면 로그를 저장한다.")
	@Test
	void test1() {
		// given
		NotificationRequestedEvent event = new NotificationRequestedEvent(
			1L,
			2L,
			NotificationType.PAYMENT_CONFIRMED.name(),
			Channel.EMAIL.name(),
			"{\"key\":\"value\"}",
			ReferenceType.PERSONAL.name()
		);

		// when
		handler.saveEvent(event);

		// then
		verify(notificationLogPersistencePort, times(1)).save(any(NotificationLog.class));
	}

	@DisplayName("알림 요청 이벤트가 발생하면 발송 명령 이벤트를 발행한다.")
	@Test
	void test2() {
		// given
		NotificationRequestedEvent event = new NotificationRequestedEvent(
			1L,
			2L,
			NotificationType.PAYMENT_CONFIRMED.name(),
			Channel.EMAIL.name(),
			"{\"key\":\"value\"}",
			ReferenceType.PERSONAL.name()
		);

		// when
		handler.publishNotification(event);

		// then
		verify(notificationEventPublisher, times(1)).publish(any(PublishCommandEvent.class));
	}
}
