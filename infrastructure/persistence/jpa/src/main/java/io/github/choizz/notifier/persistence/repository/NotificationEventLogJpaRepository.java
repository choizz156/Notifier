package io.github.choizz.notifier.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;

public interface NotificationEventLogJpaRepository extends JpaRepository<NotificationEventLogEntity, Long> {

	Optional<NotificationEventLogEntity> findFirstByNotificationIdOrderByCreatedAtDesc(Long notificationId);
	Optional<NotificationEventLogEntity> findFirstByNotificationIdAndEventStatusIsNotOrderByCreatedAtDesc(Long notificationId, EventStatus eventStatus);

	@org.springframework.data.jpa.repository.Query("""
		SELECT e.notificationId 
		FROM NotificationEventLogEntity e
		WHERE e.id IN (
			SELECT MAX(e2.id) 
			FROM NotificationEventLogEntity e2 
			GROUP BY e2.notificationId
		)
		AND e.eventStatus IN :statuses
	""")
	java.util.List<Long> findUnprocessedNotificationIds(@org.springframework.data.repository.query.Param("statuses") java.util.List<EventStatus> statuses);
}
