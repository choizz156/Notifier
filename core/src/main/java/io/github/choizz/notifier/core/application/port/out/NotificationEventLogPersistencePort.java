package io.github.choizz.notifier.core.application.port.out;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface NotificationEventLogPersistencePort {

	void save(NotificationEventLog eventLog);
	void saveAll(List<NotificationEventLog> eventLogs);
	NotificationEventLog findLatestByNotificationId(Long notificationId);
	List<Long> findUnprocessedNotificationIds(List<EventStatus> statuses, long lastId, int chunkSize);
	List<NotificationEventLog> findAllByEventStatus(EventStatus eventStatus);
	List<NotificationEventLog> findStuckLogs(long lastId, EventStatus status, Collection<NotificationType> types, LocalDateTime thresholdTime, int chunkSize);
	}

