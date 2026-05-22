package io.github.choizz.entity;

import java.time.LocalDateTime;

import io.github.choizz.notifier.domain.model.EventStatus;
import io.github.choizz.notifier.domain.model.Channel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(fluent = true)
@Table(name = "notification_event_logs")
@Entity
public class NotificationEventLogEntity extends BaseEntity {

	@Column(nullable = false)
	private Long notificationId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Channel channelType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventStatus eventStatus;

	private String failReason;

	private int retryCount;

	private boolean published;

	private LocalDateTime publishedAt;

	@Builder
	private NotificationEventLogEntity(
		Long notificationId,
		Channel channelType,
		EventStatus eventStatus,
		String failReason,
		int retryCount,
		boolean published,
		LocalDateTime publishedAt
	) {
		this.notificationId = notificationId;
		this.channelType = channelType;
		this.eventStatus = eventStatus;
		this.failReason = failReason;
		this.retryCount = retryCount;
		this.published = published;
		this.publishedAt = publishedAt;
	}
}
