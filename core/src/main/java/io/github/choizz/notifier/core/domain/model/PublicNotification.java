package io.github.choizz.notifier.core.domain.model;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
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
	private final String idempotencyKey;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private final Long version;
	private NotificationStatus status;

	@Builder
	private PublicNotification(
		Long id,
		NotificationType notificationType,
		String metadata,
		String idempotencyKey,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		Long version,
		NotificationStatus status
	) {
		this.id = id;
		this.notificationType = notificationType;
		this.metadata = metadata;
		this.idempotencyKey = idempotencyKey;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
		this.version = version;
		this.status = status != null ? status : NotificationStatus.PENDING;
	}

	public static PublicNotification of(PublicNotificationRequestedEvent event) {
		return PublicNotification.builder()
			.notificationType(NotificationType.valueOf(event.notificationType()))
			.metadata(event.metadata())
			.idempotencyKey(event.idempotentKey())
			.status(NotificationStatus.PENDING)
			.version(0L)
			.build();
	}

	public void markAsCompleted() {
		this.status = NotificationStatus.COMPLETED;
		this.updatedAt = LocalDateTime.now();
	}
}
