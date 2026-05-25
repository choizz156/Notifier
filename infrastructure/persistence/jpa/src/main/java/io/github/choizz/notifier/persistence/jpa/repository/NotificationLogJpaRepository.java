package io.github.choizz.notifier.persistence.jpa.repository;

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
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationLogEntity;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, Long> {

	Optional<NotificationLogEntity> findFirstByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(Long referenceId, ReferenceType referenceType);

	@Query("""
        SELECT e.referenceId 
        FROM NotificationLogEntity e
        WHERE e.id IN (
            SELECT MAX(e2.id) 
            FROM NotificationLogEntity e2 
            WHERE e2.referenceId > :lastReferenceId  
            GROUP BY e2.referenceId, e2.referenceType
        )
        AND e.eventStatus IN :statuses
        ORDER BY e.referenceId ASC
    """)
	List<Long> findUnprocessedNotificationIds(
		@Param("statuses") List<EventStatus> statuses,
		@Param("lastReferenceId") Long lastReferenceId,
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
