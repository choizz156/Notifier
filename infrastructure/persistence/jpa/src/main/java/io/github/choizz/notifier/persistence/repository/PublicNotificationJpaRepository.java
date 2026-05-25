package io.github.choizz.notifier.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.entity.PublicNotificationEntity;

public interface PublicNotificationJpaRepository extends JpaRepository<PublicNotificationEntity, Long> {
}
