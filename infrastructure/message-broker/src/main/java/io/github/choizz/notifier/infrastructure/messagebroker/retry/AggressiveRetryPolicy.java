package io.github.choizz.notifier.infrastructure.messagebroker.retry;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.domain.model.NotificationType;

@Component
public class AggressiveRetryPolicy implements RetryPolicy {

	private static final int MAX_RETRY_COUNT = 8;

	@Override
	public boolean support(NotificationType type) {

		return type.retryLevel() == NotificationType.RetryLevel.AGGRESSIVE;
	}

	@Override
	public int getMaxRetryCount() {

		return MAX_RETRY_COUNT;
	}

}
