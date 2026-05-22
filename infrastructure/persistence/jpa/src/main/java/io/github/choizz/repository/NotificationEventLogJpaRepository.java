package io.github.choizz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.entity.NotificationEventLogEntity;

public interface NotificationEventLogJpaRepository extends JpaRepository<NotificationEventLogEntity, Long> {

	Optional<NotificationEventLogEntity> findFirstByNotificationIdOrderByCreatedAtDesc(Long notificationId);
}
