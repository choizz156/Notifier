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
public class MessageTemplate {

	private final Long id;
	private final Channel channel;
	private final NotificationType notificationType;
	private String content;
	private boolean isActive;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private final Long version;

	@Builder
	private MessageTemplate(
		Long id,
		Channel channel,
		NotificationType notificationType,
		String content,
		boolean isActive,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		Long version
	) {
		this.id = id;
		this.channel = channel;
		this.notificationType = notificationType;
		this.content = content;
		this.isActive = isActive;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
		this.version = version != null ? version : 0L;
	}

	public static MessageTemplate create(Channel channel, NotificationType type, String content) {
		return MessageTemplate.builder()
			.channel(channel)
			.notificationType(type)
			.content(content)
			.isActive(true)
			.build();
	}

	public void updateContent(String newContent) {
		this.content = newContent;
		this.updatedAt = LocalDateTime.now();
	}

	public void changeStatus(boolean isActive) {
		this.isActive = isActive;
		this.updatedAt = LocalDateTime.now();
	}
}
