package io.github.choizz.notifier.persistence.entity;

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

	@Column(columnDefinition = "json")
	private String metadata;

	@Builder
	public PublicNotificationEntity(NotificationType notificationType, String metadata) {
		this.notificationType = notificationType;
		this.metadata = metadata;
	}

	public static PublicNotificationEntity of(NotificationType notificationType, String metadata) {
		return PublicNotificationEntity.builder()
			.notificationType(notificationType)
			.metadata(metadata)
			.build();
	}
}
