package io.github.choizz.notifier.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;

public interface NotificationEventLogJpaRepository extends JpaRepository<NotificationEventLogEntity, Long> {

	Optional<NotificationEventLogEntity> findFirstByNotificationIdOrderByCreatedAtDesc(Long notificationId);
	Optional<NotificationEventLogEntity> findFirstByNotificationIdAndEventStatusIsNotOrderByCreatedAtDesc(Long notificationId, EventStatus eventStatus);

	@Query("""
		SELECT e.notificationId 
		FROM NotificationEventLogEntity e
		WHERE e.id IN (
			SELECT MAX(e2.id) 
			FROM NotificationEventLogEntity e2 
			GROUP BY e2.notificationId
		)
		AND e.eventStatus IN :statuses
	""")
	List<Long> findUnprocessedNotificationIds(@Param("statuses") List<EventStatus> statuses);
}
