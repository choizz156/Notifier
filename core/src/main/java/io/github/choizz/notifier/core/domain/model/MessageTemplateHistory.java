package io.github.choizz.notifier.core.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode
@Getter
@Accessors(fluent = true)
public class MessageTemplateHistory {

	private final Long id;
	private final Long templateId;
	private final String content;
	private final LocalDateTime createdAt;

	@Builder
	private MessageTemplateHistory(
		Long id,
		Long templateId,
		String content,
		LocalDateTime createdAt
	) {
		this.id = id;
		this.templateId = templateId;
		this.content = content;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
	}

	public static MessageTemplateHistory createSnapshot(MessageTemplate template) {
		return MessageTemplateHistory.builder()
			.templateId(template.id())
			.content(template.content())
			.build();
	}
}
