package io.github.choizz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.entity.AlarmEntity;

public interface AlarmJpaRepository extends JpaRepository<AlarmEntity, Long> {

	Optional<AlarmEntity> findBySubscriberId(Long subscriberId);
}
