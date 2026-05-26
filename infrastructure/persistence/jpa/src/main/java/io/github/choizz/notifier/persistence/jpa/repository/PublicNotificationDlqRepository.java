package io.github.choizz.notifier.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationDlqJpaEntity;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationDlqJpaEntity.DlqStatus;

public interface PublicNotificationDlqRepository extends JpaRepository<PublicNotificationDlqJpaEntity, Long> {

	List<PublicNotificationDlqJpaEntity> findByStatusOrderByCreatedAtAsc(DlqStatus status, Pageable pageable);
}
