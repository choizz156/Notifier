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
public class NotificationEventLog {

	private final Long id;
	private final Long notificationId;
	private final NotificationType notificationType;
	private final Channel channelType;
	private final LocalDateTime createdAt;
	private final String metadata;
	private int retryCount;
	private boolean published;
	private EventStatus eventStatus;
	private String failReason;
	private LocalDateTime publishedAt;
	private LocalDateTime updatedAt;

	@Builder
	private NotificationEventLog(
		Long id,
		Long notificationId,
		NotificationType notificationType,
		Channel channelType,
		EventStatus eventStatus,
		String failReason,
		int retryCount,
		boolean published,
		LocalDateTime publishedAt,
		LocalDateTime createdAt,
		String metadata,
		LocalDateTime updatedAt
	) {

		this.id = id;
		this.notificationId = notificationId;
		this.notificationType = notificationType;
		this.channelType = channelType;
		this.eventStatus = eventStatus;
		this.failReason = failReason;
		this.retryCount = retryCount;
		this.published = published;
		this.publishedAt = publishedAt;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.metadata = metadata;
		this.updatedAt = updatedAt;
	}

	public static NotificationEventLog request(Long notificationId, NotificationType notificationType, Channel channelType, String metadata) {

		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.notificationType(notificationType)
			.channelType(channelType)
			.eventStatus(EventStatus.REQUESTED)
			.retryCount(0)
			.published(false)
			.metadata(metadata)
			.build();
	}

	public static NotificationEventLog retried(
		Long notificationId,
		NotificationType notificationType,
		Channel channelType,
		String failReason,
		String metadata,
		int retryCount
	) {

		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.notificationType(notificationType)
			.channelType(channelType)
			.eventStatus(EventStatus.RETRIED)
			.failReason(failReason)
			.retryCount(retryCount)
			.metadata(metadata)
			.published(false)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public static NotificationEventLog sent(
		Long notificationId,
		NotificationType notificationType,
		Channel channelType,
		String metadata,
		int retryCount
	) {

		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.notificationType(notificationType)
			.channelType(channelType)
			.eventStatus(EventStatus.SENT)
			.retryCount(retryCount)
			.metadata(metadata)
			.published(true)
			.publishedAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public static NotificationEventLog failed(
		Long notificationId,
		NotificationType notificationType,
		Channel channelType,
		String failReason,
		String metadata,
		int retryCount
	) {

		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.notificationType(notificationType)
			.channelType(channelType)
			.eventStatus(EventStatus.FAILED)
			.failReason(failReason)
			.retryCount(retryCount)
			.metadata(metadata)
			.published(false)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public void markAsProcessing() {
		this.eventStatus = EventStatus.PROCESSING;
		this.updatedAt = LocalDateTime.now();
	}

	public void markAsRetried(String reason, int newRetryCount) {
		this.eventStatus = EventStatus.RETRIED;
		this.failReason = reason;
		this.retryCount = newRetryCount;
		this.updatedAt = LocalDateTime.now();
	}

	public void markAsFailed(String reason, int newRetryCount) {
		this.eventStatus = EventStatus.FAILED;
		this.failReason = reason;
		this.retryCount = newRetryCount;
		this.updatedAt = LocalDateTime.now();
	}
}
