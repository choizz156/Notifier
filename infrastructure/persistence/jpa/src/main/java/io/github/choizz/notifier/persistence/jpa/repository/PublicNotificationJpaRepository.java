package io.github.choizz.notifier.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationEntity;

public interface PublicNotificationJpaRepository extends JpaRepository<PublicNotificationEntity, Long> {
}
