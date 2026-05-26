package io.github.choizz.notifier.core.application.port.out;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

public interface NotificationLogPersistencePort {

	void save(NotificationLog notificationLog);
	void saveAll(List<NotificationLog> notificationLogs);
	Optional<NotificationLog> findLatestByReferenceId(Long referenceId, ReferenceType referenceType);
	List<Long> findUnprocessedNotificationIds(List<EventStatus> statuses, long lastId, int chunkSize);
	List<NotificationLog> findAllByEventStatus(EventStatus eventStatus);
	List<NotificationLog> findStuckLogs(long lastId, EventStatus status, Collection<NotificationType> types, LocalDateTime thresholdTime, int chunkSize);
	}

