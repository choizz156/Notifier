package io.github.choizz.notifier.core.application.port.in;

import io.github.choizz.notifier.core.application.dto.NotificationContext;

public interface NotificationPushUseCase {

	void push(NotificationContext NotificationContext);
}
