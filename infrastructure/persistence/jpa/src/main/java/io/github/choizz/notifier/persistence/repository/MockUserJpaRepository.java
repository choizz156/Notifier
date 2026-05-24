package io.github.choizz.notifier.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.entity.MockUserEntity;

public interface MockUserJpaRepository extends JpaRepository<MockUserEntity, Long> {
}
