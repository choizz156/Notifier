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
	private final String idempotencyKey;
	private final String metadata;
	private NotificationStatus status;
	private String failMessage;
	private boolean isRead;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int recoverCount;
	private final Long version;

	@Builder
	private Notification(
		Long id,
		Long subscriberId,
		NotificationType notificationType,
		Channel channel,
		String idempotencyKey,
		String metadata,
		NotificationStatus status,
		String failMessage,
		boolean isRead,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		int recoverCount,
		Long version
	) {

		this.id = id;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.idempotencyKey = idempotencyKey;
		this.metadata = metadata;
		this.status = status != null ? status : NotificationStatus.PENDING;
		this.failMessage = failMessage;
		this.isRead = isRead;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
		this.recoverCount = recoverCount;
		this.version = version;
	}

	public static Notification of(NotificationContext context, Channel channel) {

		return Notification.builder()
			.subscriberId(context.subscriberId())
			.notificationType(context.notificationType())
			.channel(channel)
			.idempotencyKey(context.idempotencyKey())
			.status(NotificationStatus.PENDING)
			.metadata(context.metadataToJson())
			.isRead(false)
			.recoverCount(0)
			.version(0L)
			.build();
	}

	public void markAsCompleted() {

		if (this.status == NotificationStatus.FAILED) {
			throw new IllegalStateException("실패한 알림은 완료할 수 없습니다.");
		}
		this.status = NotificationStatus.COMPLETED;
		updateDate();
	}

	public void markAsFailed(String failReason) {

		if (this.status == NotificationStatus.FAILED) {
			throw new IllegalStateException("이미 실패한 알림입니다.");
		}
		this.status = NotificationStatus.FAILED;
		this.failMessage = failReason;
		updateDate();
	}



	public void markAsPendingForRecover() {

		if (this.status == NotificationStatus.COMPLETED) {
			throw new IllegalStateException("완료된 알림은 재시도할 수 없습니다.");
		}

		this.status = NotificationStatus.PENDING;
		this.failMessage = null;
		this.recoverCount++;
		updateDate();
	}

	private void updateDate() {

		this.updatedAt = LocalDateTime.now();
	}
}
