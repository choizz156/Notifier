package io.github.choizz.notifier.persistence.jpa.entity;

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

@Getter
@NoArgsConstructor
@Accessors(fluent = true)
@Table(
	name = "notifications",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_notification_idempotency",
			columnNames = {"subscriber_id", "notification_type", "channel", "idempotency_key"}
		)
	}
)
@Entity
public class NotificationEntity extends BaseEntity {

	@Column(nullable = false)
	private Long subscriberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Channel channel;

	@Column(name = "idempotency_key")
	private String idempotencyKey;

	@Column(columnDefinition = "json")
	private String metadata;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationStatus status;

	private String message;

	@Column(nullable = false)
	private boolean isRead;

	@Column(nullable = false)
	private int manualRetryCount;

	@Builder
	public NotificationEntity(
		Long subscriberId,
		NotificationType notificationType,
		Channel channel,
		String idempotencyKey,
		String metadata,
		NotificationStatus status,
		String message,
		boolean isRead,
		int manualRetryCount
	) {

		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.idempotencyKey = idempotencyKey;
		this.metadata = metadata;
		this.status = status;
		this.message = message;
		this.isRead = isRead;
		this.manualRetryCount = manualRetryCount;
	}
}
