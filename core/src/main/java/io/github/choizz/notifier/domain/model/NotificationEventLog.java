package io.github.choizz.notifier.domain.model;

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
	private final Channel channelType;
	private final EventStatus eventStatus;
	private final String failReason;
	private final int retryCount;
	private final LocalDateTime createdAt;
	private final String metadata;
	private boolean published;
	private LocalDateTime publishedAt;

	@Builder
	private NotificationEventLog(
		Long id,
		Long notificationId,
		Channel channelType,
		EventStatus eventStatus,
		String failReason,
		int retryCount,
		boolean published,
		LocalDateTime publishedAt,
		LocalDateTime createdAt, String metadata
	) {
		this.id = id;
		this.notificationId = notificationId;
		this.channelType = channelType;
		this.eventStatus = eventStatus;
		this.failReason = failReason;
		this.retryCount = retryCount;
		this.published = published;
		this.publishedAt = publishedAt;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.metadata = metadata;
	}

	public static NotificationEventLog request(Long notificationId, Channel channelType, String metadata) {
		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.channelType(channelType)
			.eventStatus(EventStatus.REQUESTED)
			.retryCount(0)
			.published(false)
			.metadata(metadata)
			.build();
	}

	public static NotificationEventLog fail(Long notificationId, Channel channelType, String failReason, int retryCount) {
		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.channelType(channelType)
			.eventStatus(EventStatus.FAILED)
			.failReason(failReason)
			.retryCount(retryCount)
			.published(false)
			.build();
	}

	public void markAsPublished() {
		this.published = true;
		this.publishedAt = LocalDateTime.now();
	}
}
