package io.github.choizz.notifier.rdb.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.rdb.application.retry.RetryProcessor;

@ExtendWith(MockitoExtension.class)
class RdsNotificationDispatcherTest {

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private RetryProcessor retryProcessor;

	private RdsNotificationDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		dispatcher = new RdsNotificationDispatcher(
			List.of(retryProcessor),
			applicationEventPublisher
		);
	}

	@DisplayName("알림 발행에 성공하면 PublishCompletedEvent를 발행한다.")
	@Test
	void test1() {
		// given
		NotifierPort notifierPort = mock(NotifierPort.class);
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.subscriberId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.metadata("{}")
			.retryCount(0)
			.build();

		when(retryProcessor.support(NotificationType.PAYMENT_CONFIRMED)).thenReturn(true);

		// when
		dispatcher.dispatch(notifierPort, context);

		// then
		verify(notifierPort, times(1)).publish(context);
		verify(applicationEventPublisher, times(1)).publishEvent(any(PublishCompletedEvent.class));
		verify(retryProcessor, times(0)).handle(any(), any());
	}

	@DisplayName("알림 발행에 실패하면 RetryProcessor에게 처리를 위임한다.")
	@Test
	void test2() {
		// given
		NotifierPort notifierPort = mock(NotifierPort.class);
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.subscriberId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.metadata("{}")
			.retryCount(0)
			.build();

		when(retryProcessor.support(NotificationType.PAYMENT_CONFIRMED)).thenReturn(true);
		doThrow(new RuntimeException("API Timeout")).when(notifierPort).publish(context);

		// when
		dispatcher.dispatch(notifierPort, context);

		// then
		verify(notifierPort, times(1)).publish(context);
		verify(applicationEventPublisher, times(0)).publishEvent(any(PublishCompletedEvent.class));
		verify(retryProcessor, times(1)).handle(eq(notifierPort), any(PublicationContext.class));
	}
}
