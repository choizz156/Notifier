package io.github.choizz.notifier.core.application.port.out;

import java.util.List;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;

public interface NotificationEventLogPersistencePort {

	void save(NotificationEventLog eventLog);
	NotificationEventLog findLatestByNotificationId(Long notificationId);
	List<Long> findUnprocessedNotificationIds(List<EventStatus> statuses, long lastId, int chunkSize);
	List<NotificationEventLog> findAllByEventStatus(EventStatus eventStatus);
}
