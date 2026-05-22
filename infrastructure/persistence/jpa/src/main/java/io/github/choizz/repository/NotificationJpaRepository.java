package io.github.choizz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.entity.NotificationEntity;
import io.github.choizz.notifier.domain.model.NotificationStatus;
import io.github.choizz.notifier.domain.model.NotificationType;
import io.github.choizz.notifier.domain.model.Channel;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

	Optional<NotificationEntity> findBySubscriberId(Long subscriberId);

	boolean existsBySubscriberIdAndNotificationTypeAndChannelAndStatusIn(
		Long subscriberId,
		NotificationType notificationType,
		Channel channel,
		List<NotificationStatus> statuses
	);
}
