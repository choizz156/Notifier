package io.github.choizz.entity;

import io.github.choizz.notifier.domain.model.NotificationStatus;
import io.github.choizz.notifier.domain.model.NotificationType;
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

@Entity
@Table(name = "Notifications")
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

	private int retryCount;

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
		int retryCount,
		boolean isRead
	) {

		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.metadata = metadata;
		this.status = status;
		this.message = message;
		this.retryCount = retryCount;
		this.isRead = isRead;
	}
}
