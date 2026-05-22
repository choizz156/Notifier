package io.github.choizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.entity.NotificationEventLogEntity;

public interface NotificationEventLogJpaRepository extends JpaRepository<NotificationEventLogEntity, Long> {
}
