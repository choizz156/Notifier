package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.domain.model.NotificationEventLog;

public interface NotificationEventLogPersistencePort {

	void save(NotificationEventLog eventLog);
	NotificationEventLog findLatestByNotificationId(Long notificationId);
	java.util.List<Long> findUnprocessedNotificationIds(java.util.List<io.github.choizz.notifier.core.domain.model.EventStatus> statuses);
}
