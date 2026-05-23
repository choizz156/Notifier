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

	public static NotificationEventLog request(Long notificationId, Channel channelType, String metadata) {

		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.channelType(channelType)
			.eventStatus(EventStatus.REQUESTED)
			.retryCount(0)
			.published(false)
			.metadata(metadata)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public void markAsPublished() {
		this.published = true;
		this.eventStatus = EventStatus.SENT;
		this.publishedAt = LocalDateTime.now();
		updateDate();
	}

	public void markAsRetried() {
		this.eventStatus = EventStatus.RETRIED;
		this.retryCount++;
		updateDate();
	}

	public void markAsFailed() {
		this.eventStatus = EventStatus.FAILED;
		updateDate();
	}

	private void updateDate() {
		this.updatedAt = LocalDateTime.now();
	}
}
