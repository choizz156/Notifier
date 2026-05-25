package io.github.choizz.notifier.rdb.application.retry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.RetryLevel;

@ExtendWith(MockitoExtension.class)
class StandardRetryProcessorTest {

	@Mock
	private NotificationLogUseCase notificationLogUseCase;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private StandardRetryProcessor processor;

	@DisplayName("지원하는 NotificationType인 경우 true를 반환한다.")
	@Test
	void test1() {
		boolean support = processor.support(NotificationType.COUPON_ISSUED); // STANDARD
		assertThat(support).isTrue();
	}

	@DisplayName("RDB 재시도 레벨을 확인한다.")
	@Test
	void test2() {
		assertThat(processor.getRdbRetryLevel()).isEqualTo(RetryLevel.STANDARD);
	}
}
