package io.github.choizz.notifier.persistence.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.NotificationLogEntity;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, Long> {

	Optional<NotificationLogEntity> findFirstByNotificationIdOrderByCreatedAtDesc(Long notificationId);

	@Query("""
        SELECT e.notificationId 
        FROM NotificationLogEntity e
        WHERE e.id IN (
            SELECT MAX(e2.id) 
            FROM NotificationLogEntity e2 
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

	List<NotificationLogEntity> findAllByEventStatus(EventStatus eventStatus);

	@Query("""
		SELECT e FROM NotificationLogEntity e
		WHERE e.id > :lastId
		AND e.eventStatus = :status
		AND e.notificationType IN :types
		AND e.updatedAt < :thresholdTime
		ORDER BY e.id ASC
	""")
	List<NotificationLogEntity> findStuckLogs(
		@Param("lastId") Long lastId,
		@Param("status") EventStatus status,
		@Param("types") Collection<NotificationType> types,
		@Param("thresholdTime") LocalDateTime thresholdTime,
		Limit limit
	);
}
