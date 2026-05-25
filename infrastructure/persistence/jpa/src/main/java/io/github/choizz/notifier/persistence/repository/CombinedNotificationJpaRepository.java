package io.github.choizz.notifier.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import io.github.choizz.notifier.persistence.entity.NotificationEntity;

public interface CombinedNotificationJpaRepository extends Repository<NotificationEntity, Long> {

	@Query(value = """
		SELECT * FROM (
			SELECT 
				id, 
				'PERSONAL' as reference_type,
				subscriber_id,
				notification_type, 
				channel,
				status,
				message,
				is_read, 
				created_at,
				manual_retry_count
			FROM notifications
			WHERE subscriber_id = :subscriberId
			
			UNION ALL
			
			SELECT 
				p.id, 
				'PUBLIC' as reference_type,
				:subscriberId as subscriber_id,
				p.notification_type, 
				'NONE' as channel,
				'COMPLETED' as status,
				p.message, 
				CASE WHEN r.id IS NOT NULL THEN true ELSE false END as is_read, 
				p.created_at,
				0 as manual_retry_count
			FROM public_notifications p
			LEFT JOIN public_notification_receipts r 
			  ON p.id = r.public_notification_id AND r.subscriber_id = :subscriberId
		) combined
		ORDER BY created_at DESC
		""",
		countQuery = """
		SELECT COUNT(*) FROM (
			SELECT id FROM notifications WHERE subscriber_id = :subscriberId
			UNION ALL
			SELECT id FROM public_notifications
		) combined
		""",
		nativeQuery = true)
	Page<CombinedNotificationProjection> findCombinedNotifications(
		@Param("subscriberId") Long subscriberId,
		Pageable pageable
	);

	@Query(value = """
		SELECT * FROM (
			SELECT 
				id, 
				'PERSONAL' as reference_type,
				subscriber_id,
				notification_type, 
				channel,
				status,
				message,
				is_read, 
				created_at,
				manual_retry_count
			FROM notifications
			WHERE subscriber_id = :subscriberId
			
			UNION ALL
			
			SELECT 
				p.id, 
				'PUBLIC' as reference_type,
				:subscriberId as subscriber_id,
				p.notification_type, 
				'NONE' as channel,
				'COMPLETED' as status,
				p.message, 
				CASE WHEN r.id IS NOT NULL THEN true ELSE false END as is_read, 
				p.created_at,
				0 as manual_retry_count
			FROM public_notifications p
			LEFT JOIN public_notification_receipts r 
			  ON p.id = r.public_notification_id AND r.subscriber_id = :subscriberId
		) combined
		WHERE is_read = :isRead
		ORDER BY created_at DESC
		""",
		countQuery = """
		SELECT COUNT(*) FROM (
			SELECT id, is_read
			FROM notifications
			WHERE subscriber_id = :subscriberId
			
			UNION ALL
			
			SELECT p.id, 
				   CASE WHEN r.id IS NOT NULL THEN true ELSE false END as is_read
			FROM public_notifications p
			LEFT JOIN public_notification_receipts r 
			  ON p.id = r.public_notification_id AND r.subscriber_id = :subscriberId
		) combined
		WHERE is_read = :isRead
		""",
		nativeQuery = true)
	Page<CombinedNotificationProjection> findCombinedNotificationsByIsRead(
		@Param("subscriberId") Long subscriberId,
		@Param("isRead") Boolean isRead,
		Pageable pageable
	);
}
