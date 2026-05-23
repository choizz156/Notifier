package io.github.choizz.notifier.persistence.entity;

import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.Channel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(
	name = "notifications",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_notification_duplicate",
			columnNames = {"subscriber_id", "notification_type", "channel", "status"}
		)
	}
)
@Getter
@NoArgsConstructor
@Accessors(fluent = true)
public class NotificationEntity extends BaseEntity {

	@Column(nullable = false)
	private Long subscriberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Channel channel;

	@Column(columnDefinition = "json")
	private String metadata;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationStatus status;

	private String message;

	@Column(nullable = false)
	private boolean isRead;

	@Builder
	public NotificationEntity(
		Long subscriberId,
		NotificationType notificationType,
		Channel channel,
		String metadata,
		NotificationStatus status,
		String message,
		boolean isRead
	) {

		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.metadata = metadata;
		this.status = status;
		this.message = message;
		this.isRead = isRead;
	}
}
