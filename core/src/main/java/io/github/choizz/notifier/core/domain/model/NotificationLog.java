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
public class NotificationLog {

	private final Long id;
	private final Long referenceId;
	private final ReferenceType referenceType;
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
	private NotificationLog(
		Long id,
		Long referenceId,
		ReferenceType referenceType,
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
		this.referenceId = referenceId;
		this.referenceType = referenceType;
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

	public static NotificationLog request(Long referenceId, ReferenceType referenceType, NotificationType notificationType, Channel channelType, String metadata) {

		return NotificationLog.builder()
			.referenceId(referenceId)
			.referenceType(referenceType)
			.notificationType(notificationType)
			.channelType(channelType)
			.eventStatus(EventStatus.REQUESTED)
			.retryCount(0)
			.published(false)
			.metadata(metadata)
			.build();
	}

	public static NotificationLog retried(
		Long referenceId,
		ReferenceType referenceType,
		NotificationType notificationType,
		Channel channelType,
		String failReason,
		String metadata,
		int retryCount
	) {

		return NotificationLog.builder()
			.referenceId(referenceId)
			.referenceType(referenceType)
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

	public static NotificationLog sent(
		Long referenceId,
		ReferenceType referenceType,
		NotificationType notificationType,
		Channel channelType,
		String metadata,
		int retryCount
	) {

		return NotificationLog.builder()
			.referenceId(referenceId)
			.referenceType(referenceType)
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

	public static NotificationLog failed(
		Long referenceId,
		ReferenceType referenceType,
		NotificationType notificationType,
		Channel channelType,
		String failReason,
		String metadata,
		int retryCount
	) {

		return NotificationLog.builder()
			.referenceId(referenceId)
			.referenceType(referenceType)
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
