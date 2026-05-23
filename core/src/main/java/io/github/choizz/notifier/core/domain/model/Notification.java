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
	private int retryCount;
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
		int retryCount,
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
		this.retryCount = retryCount;
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
			.retryCount(0)
			.isRead(false)
			.build();
	}

	public void markAsCompleted() {
		this.status = NotificationStatus.COMPLETED;
		updateDate();
	}

	public void markAsFailed() {
		if (this.status == NotificationStatus.COMPLETED) {
			throw new IllegalStateException("COMPLETED 상태에서는 FAILED이 될 수 없습니다.");
		}
		this.status = NotificationStatus.FAILED;
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
