package io.github.choizz.notifier.infrastructure.messagebroker.retry;

import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface RetryPolicy {

	boolean support(NotificationType type);
	int getMaxRetryCount();
}
