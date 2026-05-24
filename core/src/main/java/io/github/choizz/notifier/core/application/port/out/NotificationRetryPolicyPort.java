package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface NotificationRetryPolicyPort {

	int getMaxAttempts(NotificationType.RetryLevel retryLevel);

	long getMaxProcessingTimeSeconds(NotificationType.RetryLevel retryLevel);
}
