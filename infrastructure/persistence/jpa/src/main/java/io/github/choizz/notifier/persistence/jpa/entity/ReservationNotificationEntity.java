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

	@Column(nullable = false)
	private Long subscriberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Column(nullable = false)
	private LocalDateTime reservationTime;

	@Column(nullable = false)
	private boolean isPublished;

	@Builder
	public ReservationNotificationEntity(
		Long id,
		Long subscriberId,
		NotificationType notificationType,
		LocalDateTime reservationTime,
		boolean isPublished
	) {
		this.id(id);
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.reservationTime = reservationTime;
		this.isPublished = isPublished;
	}
}
