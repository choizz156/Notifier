package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.domain.model.NotificationEventLog;

public interface NotificationEventLogPersistencePort {

	NotificationEventLog save(NotificationEventLog eventLog);

	NotificationEventLog findLatestByNotificationId(Long notificationId);
}
