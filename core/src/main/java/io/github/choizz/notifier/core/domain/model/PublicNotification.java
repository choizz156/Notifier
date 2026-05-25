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
public class PublicNotification {

	private final Long id;
	private final NotificationType notificationType;
	private final String metadata;
	private final String message;
	private final LocalDateTime createdAt;

	@Builder
	private PublicNotification(
		Long id,
		NotificationType notificationType,
		String metadata,
		String message,
		LocalDateTime createdAt
	) {
		this.id = id;
		this.notificationType = notificationType;
		this.metadata = metadata;
		this.message = message;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
	}
}
