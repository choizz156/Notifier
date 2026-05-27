package io.github.choizz.notifier.persistence.jpa.entity;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.persistence.jpa.converter.ChannelConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(fluent = true)
@Table(
	name = "notification_event_logs",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_notification_logs_duplicate",
			columnNames = {"reference_id", "reference_type", "channel_type", "subscriber_id", "event_status", "retry_count"}
		)
	}
)
@Entity
public class NotificationLogEntity extends BaseEntity {

	@Column(nullable = false)
	private Long referenceId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReferenceType referenceType;

	private Long subscriberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Convert(converter = ChannelConverter.class)
	@Column(nullable = false)
	private Channel channelType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventStatus eventStatus;

	private String failReason;

	private int retryCount;

	private boolean published;

	private LocalDateTime publishedAt;

	@Column(columnDefinition = "TEXT")
	private String metadata;

	@Builder
	private NotificationLogEntity(
		Long referenceId,
		ReferenceType referenceType,
		Long subscriberId,
		NotificationType notificationType,
		Channel channelType,
		EventStatus eventStatus,
		String failReason,
		int retryCount,
		boolean published,
		LocalDateTime publishedAt,
		String metadata
	) {
		this.referenceId = referenceId;
		this.referenceType = referenceType;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channelType = channelType;
		this.eventStatus = eventStatus;
		this.failReason = failReason;
		this.retryCount = retryCount;
		this.published = published;
		this.publishedAt = publishedAt;
		this.metadata = metadata;
	}
}
