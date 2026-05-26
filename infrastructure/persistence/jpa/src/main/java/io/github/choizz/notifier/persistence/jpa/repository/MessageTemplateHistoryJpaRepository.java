package io.github.choizz.notifier.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateHistoryEntity;

public interface MessageTemplateHistoryJpaRepository extends JpaRepository<MessageTemplateHistoryEntity, Long> {

	List<MessageTemplateHistoryEntity> findByTemplateIdOrderByCreatedAtDesc(Long templateId);
}
