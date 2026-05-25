package io.github.choizz.notifier.rdb.application.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;

@SpringBootTest(
	classes = AggressiveRetryIntegrationTest.RetryTestConfig.class,
	properties = {
		"notifier.retry.aggressive.max-attempts=3",
		"notifier.retry.aggressive.delay=10",
		"notifier.retry.aggressive.multiplier=1.0",
		"notifier.retry.aggressive.max-delay=10"
	}
)
@RecordApplicationEvents
class AggressiveRetryIntegrationTest {

	@Configuration
	@EnableRetry
	@EnableAspectJAutoProxy(proxyTargetClass = true)
	static class RetryTestConfig {
		@Bean
		public AggressiveRetryProcessor aggressiveRetryProcessor(
			NotificationLogUseCase notificationLogUseCase,
			ApplicationEventPublisher applicationEventPublisher
		) {
			return new AggressiveRetryProcessor(notificationLogUseCase, applicationEventPublisher);
		}
	}

	@Autowired
	private AggressiveRetryProcessor retryProcessor;

	@MockitoBean
	private NotificationLogUseCase notificationLogUseCase;

	@MockitoBean
	private NotifierPort notifierPort;

	@Autowired
	private ApplicationEvents applicationEvents;

	@DisplayName("재시도 실패 시 최대 시도 횟수만큼 재시도 후 Recover 메서드가 호출된다.")
	@Test
	void test1() {
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.subscriberId(100L)
			.notificationType("PAYMENT_CONFIRMED")
			.channel("EMAIL")
			.metadata("{}")
			.retryCount(0)
			.build();

		doThrow(new RuntimeException("Publish Error")).when(notifierPort).publish(context);

		retryProcessor.handle(notifierPort, context);

		verify(notifierPort, times(3)).publish(context);
		
		long failedEventCount = applicationEvents.stream(PublishFailedEvent.class).count();
		assertThat(failedEventCount).isEqualTo(1);
	}
}
