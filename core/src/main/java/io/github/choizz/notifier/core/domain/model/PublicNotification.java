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
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	private PublicNotification(
		Long id,
		NotificationType notificationType,
		String metadata,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		this.id = id;
		this.notificationType = notificationType;
		this.metadata = metadata;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
	}

	public static PublicNotification of(NotificationType notificationType, String metadata) {
		return PublicNotification.builder()
			.notificationType(notificationType)
			.metadata(metadata)
			.build();
	}
}
