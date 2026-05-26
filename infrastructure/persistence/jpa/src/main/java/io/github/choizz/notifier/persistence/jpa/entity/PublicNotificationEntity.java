package io.github.choizz.notifier.persistence.jpa.entity;

import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@Table(name = "public_notifications")
@Entity
public class PublicNotificationEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Column(length = 2000)
	private String metadata;

	@Column(nullable = false, unique = true)
	private String idempotencyKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationStatus status;

	@Builder
	public PublicNotificationEntity(
		NotificationType notificationType,
		String metadata,
		String idempotencyKey,
		NotificationStatus status
	) {
		this.notificationType = notificationType;
		this.metadata = metadata;
		this.idempotencyKey = idempotencyKey;
		this.status = status != null ? status : NotificationStatus.PENDING;
	}

	public void updateStatus(NotificationStatus status) {
		this.status = status;
	}
}
