package io.github.choizz.notifier.rdb.application.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.RetryLevel;

@ExtendWith(MockitoExtension.class)
class AggressiveRetryProcessorTest {

	@Mock
	private NotificationLogUseCase notificationLogUseCase;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private AggressiveRetryProcessor processor;

	@DisplayName("지원하는 NotificationType인 경우 true를 반환한다.")
	@Test
	void test1() {
		boolean support = processor.support(NotificationType.PAYMENT_CONFIRMED);
		assertThat(support).isTrue();
	}

	@DisplayName("지원하지 않는 NotificationType인 경우 false를 반환한다.")
	@Test
	void test2() {
		boolean support = processor.support(NotificationType.COURSE_START_REMINDER);
		assertThat(support).isFalse();
	}

	@DisplayName("재시도를 수행하고 완료 이벤트를 발행한다.")
	@Test
	void test3() {
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

		RetryContext retryContext = mock(RetryContext.class);
		RetrySynchronizationManager.register(retryContext);

		try {
			// when
			processor.handle(notifierPort, context);

			// then
			verify(notificationLogUseCase, times(1)).savenotificationLog(eq(1L), eq(EventStatus.RETRIED), any(PublicationContext.class));
			verify(notifierPort, times(1)).publish(context);
			verify(applicationEventPublisher, times(1)).publishEvent(any(PublishCompletedEvent.class));
		} finally {
			RetrySynchronizationManager.clear();
		}
	}

	@DisplayName("재시도 실패 시 복구(recover) 메서드가 호출된다.")
	@Test
	void test4() {
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

		// when
		processor.fail(new RuntimeException("failed"), notifierPort, context);

		// then
		verify(applicationEventPublisher, times(1)).publishEvent(any(PublishFailedEvent.class));
	}
	
	@DisplayName("RDB 재시도 레벨을 확인한다.")
	@Test
	void test5() {
		assertThat(processor.getRdbRetryLevel()).isEqualTo(RetryLevel.AGGRESSIVE);
	}
}
