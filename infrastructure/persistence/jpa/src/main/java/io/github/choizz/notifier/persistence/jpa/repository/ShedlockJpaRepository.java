package io.github.choizz.notifier.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.jpa.entity.ShedlockEntity;

public interface ShedlockJpaRepository extends JpaRepository<ShedlockEntity, String> {
}
