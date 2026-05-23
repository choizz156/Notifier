package io.github.choizz.notifier.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;

public interface NotificationEventLogJpaRepository extends JpaRepository<NotificationEventLogEntity, Long> {

	Optional<NotificationEventLogEntity> findFirstByNotificationIdOrderByCreatedAtDesc(Long notificationId);
}
