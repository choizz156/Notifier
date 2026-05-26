package io.github.choizz.notifier.persistence.jpa.entity;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.NotificationType;
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
@Table(name = "reservation_notifications")
@Entity
public class ReservationNotificationEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Column(columnDefinition = "TEXT")
	private String metadata;

	@Column(nullable = false)
	private LocalDateTime reservationTime;

	@Column(nullable = false)
	private boolean isPublished;

	@Builder
	public ReservationNotificationEntity(
		Long id,
		NotificationType notificationType,
		String metadata,
		LocalDateTime reservationTime,
		boolean isPublished
	) {
		this.id(id);
		this.notificationType = notificationType;
		this.metadata = metadata;
		this.reservationTime = reservationTime;
		this.isPublished = isPublished;
	}
}
