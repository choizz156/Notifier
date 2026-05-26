package io.github.choizz.notifier.persistence.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.github.choizz.notifier.admin.application.AdminMessageTemplateRepository;
import io.github.choizz.notifier.core.application.port.out.MessageTemplatePersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateEntity;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateHistoryEntity;
import io.github.choizz.notifier.persistence.jpa.repository.MessageTemplateHistoryJpaRepository;
import io.github.choizz.notifier.persistence.jpa.repository.MessageTemplateJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MessageTemplatePersistenceAdapter implements MessageTemplatePersistencePort, AdminMessageTemplateRepository {

	private final MessageTemplateJpaRepository messageTemplateJpaRepository;
	private final MessageTemplateHistoryJpaRepository messageTemplateHistoryJpaRepository;

	@Override
	public MessageTemplate save(MessageTemplate template) {
		MessageTemplateEntity entity = MessageTemplateMapper.toEntity(template);
		MessageTemplateEntity savedEntity = messageTemplateJpaRepository.save(entity);
		return MessageTemplateMapper.toDomain(savedEntity);
	}

	@Override
	public MessageTemplateHistory saveHistory(MessageTemplateHistory history) {
		MessageTemplateHistoryEntity entity = MessageTemplateMapper.toEntity(history);
		MessageTemplateHistoryEntity savedEntity = messageTemplateHistoryJpaRepository.save(entity);
		return MessageTemplateMapper.toDomain(savedEntity);
	}

	@Override
	public Optional<MessageTemplate> findByChannelAndNotificationType(Channel channel, NotificationType type) {
		return messageTemplateJpaRepository.findByChannelAndNotificationType(channel, type)
			.map(MessageTemplateMapper::toDomain);
	}

	@Override
	public MessageTemplate findById(Long id) {
		return messageTemplateJpaRepository.findById(id)
			.map(MessageTemplateMapper::toDomain)
			.orElseThrow(() -> new IllegalArgumentException("템플릿을 찾을 수 없습니다. id=" + id));
	}

	@Override
	public List<MessageTemplate> findAll() {
		return messageTemplateJpaRepository.findAll().stream()
			.map(MessageTemplateMapper::toDomain)
			.toList();
	}

	@Override
	public List<MessageTemplateHistory> findHistoriesByTemplateId(Long templateId) {
		return messageTemplateHistoryJpaRepository.findByTemplateIdOrderByCreatedAtDesc(templateId).stream()
			.map(MessageTemplateMapper::toDomain)
			.toList();
	}
}
