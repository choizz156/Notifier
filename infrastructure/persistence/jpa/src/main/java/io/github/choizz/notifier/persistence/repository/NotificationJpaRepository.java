package io.github.choizz.notifier.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.NotificationEntity;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

	Optional<NotificationEntity> findBySubscriberId(Long subscriberId);

	boolean existsBySubscriberIdAndNotificationTypeAndChannelAndStatus(
		Long subscriberId,
		NotificationType notificationType,
		Channel channel,
		NotificationStatus statuses
	);

	org.springframework.data.domain.Page<NotificationEntity> findBySubscriberId(
		Long subscriberId,
		org.springframework.data.domain.Pageable pageable
	);

	org.springframework.data.domain.Page<NotificationEntity> findBySubscriberIdAndIsRead(
		Long subscriberId,
		boolean isRead,
		org.springframework.data.domain.Pageable pageable
	);
}
