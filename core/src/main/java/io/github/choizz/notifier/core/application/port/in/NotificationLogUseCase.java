package io.github.choizz.notifier.core.application.port.in;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;

public interface NotificationLogUseCase {

	void updateStatus(NotificationEventLog notificationEventLog, EventStatus eventStatus);
}
