package io.github.choizz.notifier.domain.model;

import java.time.LocalDateTime;

import io.github.choizz.notifier.application.dto.NotificationContext;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode
@Getter
@Accessors(fluent = true)
public class Notification {

	private final Long id;
	private final Long subscriberId;
	private final NotificationType notificationType;
	private final Channel channel;
	private final String metadata;
	private NotificationStatus status;
	private String message;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	private Notification(
		Long id,
		Long subscriberId,
		NotificationType notificationType,
		Channel channel,
		String metadata,
		NotificationStatus status,
		String message,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {

		this.id = id;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.metadata = metadata;
		this.status = status != null ? status : NotificationStatus.PENDING;
		this.message = null;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
	}

	public static Notification from(NotificationContext context) {
		return Notification.builder()
			.subscriberId(context.subscriberId())
			.notificationType(context.notificationType())
			.channel(context.channel())
			.status(NotificationStatus.PENDING)
			.metadata(context.metadataToJson())
			.build();
	}

	public void markAsCompleted() {
		this.status = NotificationStatus.COMPLETED;
	}

	public void markAsFailed() {
		if (this.status == NotificationStatus.COMPLETED) {
			throw new IllegalStateException("COMPLETED 상태에서는 FAILED이 될 수 없습니다.");
		}
		this.status = NotificationStatus.FAILED;
	}

	public void markAsRetrying() {
		if (this.status != NotificationStatus.FAILED) {
			throw new IllegalStateException("FAILED 상태에서만 RETRYING으로 전환 가능합니다.");
		}
		this.status = NotificationStatus.RETRYING;
	}

	public void markAsSending() {
		this.status = NotificationStatus.SENDING;
	}

	public void applyMessage(String message){
		this.message = message;
	}
}
