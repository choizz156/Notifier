package io.github.choizz.notifier.core.domain.model;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
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
	private String failMessage;
	private boolean isRead;
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
		String failMessage,
		boolean isRead,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {

		this.id = id;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.metadata = metadata;
		this.status = status != null ? status : NotificationStatus.PENDING;
		this.failMessage = null;
		this.isRead = isRead;
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
			.isRead(false)
			.build();
	}

	public void markAsCompleted() {
		this.status = NotificationStatus.COMPLETED;
		updateDate();
	}

	public void markAsFailed(String failReason) {
		this.status = NotificationStatus.FAILED;
		this.failMessage = failReason;
		updateDate();
	}

	public void markAsRetrying() {
		this.status = NotificationStatus.RETRYING;
		updateDate();
	}

	public void applyMessage(String message){
		this.failMessage = message;
		updateDate();
	}

	public void markAsRead() {
		this.isRead = true;
		updateDate();
	}

	private void updateDate() {

		this.updatedAt = LocalDateTime.now();
	}
}
