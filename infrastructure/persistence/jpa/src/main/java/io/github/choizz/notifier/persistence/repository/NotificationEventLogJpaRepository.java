package io.github.choizz.notifier.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
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
            WHERE e2.notificationId > :lastNotificationId  
            GROUP BY e2.notificationId
        )
        AND e.eventStatus IN :statuses
        ORDER BY e.notificationId ASC
    """)
	List<Long> findUnprocessedNotificationIds(
		@Param("statuses") List<EventStatus> statuses,
		@Param("lastNotificationId") Long lastNotificationId,
		Limit limit
	);
}
