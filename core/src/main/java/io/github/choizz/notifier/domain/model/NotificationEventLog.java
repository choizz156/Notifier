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
	private final EventType eventType;
	private final EventStatus eventStatus;
	private final String failReason;
	private boolean published;
	private LocalDateTime publishedAt;
	private final LocalDateTime createdAt;

	@Builder
	private NotificationEventLog(
		Long id,
		Long notificationId,
		EventType eventType,
		EventStatus eventStatus,
		String failReason,
		boolean published,
		LocalDateTime publishedAt,
		LocalDateTime createdAt
	) {
		this.id = id;
		this.notificationId = notificationId;
		this.eventType = eventType;
		this.eventStatus = eventStatus;
		this.failReason = failReason;
		this.published = published;
		this.publishedAt = publishedAt;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
	}

	public static NotificationEventLog success(Long notificationId, EventType eventType) {
		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.eventType(eventType)
			.eventStatus(EventStatus.SUCCESS)
			.published(false)
			.build();
	}

	public static NotificationEventLog fail(Long notificationId, EventType eventType, String failReason) {
		return NotificationEventLog.builder()
			.notificationId(notificationId)
			.eventType(eventType)
			.eventStatus(EventStatus.FAILED)
			.failReason(failReason)
			.published(false)
			.build();
	}

	public void markAsPublished() {
		this.published = true;
		this.publishedAt = LocalDateTime.now();
	}
}
