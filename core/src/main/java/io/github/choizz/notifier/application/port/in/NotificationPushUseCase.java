package io.github.choizz.notifier.application.port.in;

import io.github.choizz.notifier.application.dto.NotificationContext;

public interface NotificationPushUseCase {

	void push(NotificationContext NotificationContext);
}
