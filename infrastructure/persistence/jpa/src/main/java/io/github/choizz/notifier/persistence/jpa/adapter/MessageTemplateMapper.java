package io.github.choizz.notifier.persistence.jpa.adapter;

import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateEntity;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateHistoryEntity;

public class MessageTemplateMapper {

	public static MessageTemplateEntity toEntity(MessageTemplate domain) {
		MessageTemplateEntity entity = MessageTemplateEntity.builder()
			.channel(domain.channel())
			.notificationType(domain.notificationType())
			.content(domain.content())
			.isActive(domain.isActive())
			.build();
		entity.id(domain.id());
		entity.updatedAt(domain.updatedAt());
		entity.version(domain.version());
		return entity;
	}

	public static MessageTemplate toDomain(MessageTemplateEntity entity) {
		return MessageTemplate.builder()
			.id(entity.id())
			.channel(entity.channel())
			.notificationType(entity.notificationType())
			.content(entity.content())
			.isActive(entity.isActive())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.version(entity.version())
			.build();
	}

	public static MessageTemplateHistoryEntity toEntity(MessageTemplateHistory domain) {
		MessageTemplateHistoryEntity entity = MessageTemplateHistoryEntity.builder()
			.templateId(domain.templateId())
			.content(domain.content())
			.build();
		entity.id(domain.id());
		return entity;
	}

	public static MessageTemplateHistory toDomain(MessageTemplateHistoryEntity entity) {
		return MessageTemplateHistory.builder()
			.id(entity.id())
			.templateId(entity.templateId())
			.content(entity.content())
			.createdAt(entity.createdAt())
			.build();
	}
}
