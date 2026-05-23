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
	private int manualRetryCount;

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
		LocalDateTime updatedAt,
		int manualRetryCount
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
		this.manualRetryCount = manualRetryCount;
	}

	public static Notification from(NotificationContext context) {

		return Notification.builder()
			.subscriberId(context.subscriberId())
			.notificationType(context.notificationType())
			.channel(context.channel())
			.status(NotificationStatus.PENDING)
			.metadata(context.metadataToJson())
			.isRead(false)
			.manualRetryCount(0)
			.build();
	}

	public void markAsCompleted() {

		verifyTransition(NotificationStatus.COMPLETED);
		this.status = NotificationStatus.COMPLETED;
		updateDate();
	}

	public void markAsFailed(String failReason) {

		verifyTransition(NotificationStatus.FAILED);
		this.status = NotificationStatus.FAILED;
		this.failMessage = failReason;
		updateDate();
	}

	public void markAsRetrying() {

		verifyTransition(NotificationStatus.RETRYING);
		this.status = NotificationStatus.RETRYING;
		updateDate();
	}

	public void markAsPendingForManualRetry() {

		verifyTransition(NotificationStatus.PENDING);
		this.status = NotificationStatus.PENDING;
		this.failMessage = null;
		this.manualRetryCount++;
		updateDate();
	}

	public void applyFailMessage(String failMessage) {
		this.failMessage = failMessage;
		updateDate();
	}

	public void markAsRead() {

		this.isRead = true;
		updateDate();
	}

	private void updateDate() {

		this.updatedAt = LocalDateTime.now();
	}

	private void verifyTransition(NotificationStatus nextStatus) {

		if (this.status == nextStatus) {
			return;
		}
		if (this.status == NotificationStatus.COMPLETED) {
			throw new IllegalStateException("이미 전송 완료된 알림의 상태는 변경할 수 없습니다.");
		}
		if (this.status == NotificationStatus.FAILED && nextStatus != NotificationStatus.PENDING) {
			throw new IllegalStateException("실패한 알림은 수동 재시도만 가능합니다.");
		}
		if (nextStatus == NotificationStatus.PENDING && this.status != NotificationStatus.FAILED) {
			throw new IllegalStateException("수동 재시도는 실패한 알림에 대해서만 가능합니다.");
		}
	}
}
