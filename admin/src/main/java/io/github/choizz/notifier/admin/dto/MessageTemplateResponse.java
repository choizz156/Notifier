package io.github.choizz.notifier.admin.dto;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public record MessageTemplateResponse(
	Long id,
	Channel channel,
	NotificationType type,
	String content,
	boolean isActive,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static MessageTemplateResponse from(MessageTemplate template) {
		return new MessageTemplateResponse(
			template.id(),
			template.channel(),
			template.notificationType(),
			template.content(),
			template.isActive(),
			template.createdAt(),
			template.updatedAt()
		);
	}
}
