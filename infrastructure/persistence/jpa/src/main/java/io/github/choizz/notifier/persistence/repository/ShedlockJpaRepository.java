package io.github.choizz.notifier.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.entity.ShedlockEntity;

public interface ShedlockJpaRepository extends JpaRepository<ShedlockEntity, String> {
}
