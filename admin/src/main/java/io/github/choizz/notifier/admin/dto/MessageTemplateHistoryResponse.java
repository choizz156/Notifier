package io.github.choizz.notifier.admin.dto;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;

public record MessageTemplateHistoryResponse(
	Long id,
	Long templateId,
	String content,
	LocalDateTime createdAt
) {
	public static MessageTemplateHistoryResponse from(MessageTemplateHistory history) {
		return new MessageTemplateHistoryResponse(
			history.id(),
			history.templateId(),
			history.content(),
			history.createdAt()
		);
	}
}
