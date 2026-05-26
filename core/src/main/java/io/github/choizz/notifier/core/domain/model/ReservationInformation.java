package io.github.choizz.notifier.core.domain.model;

import java.time.LocalDateTime;
import java.util.EnumSet;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode
@Getter
@Accessors(fluent = true)
public class ReservationInformation {

	private static final EnumSet<NotificationType> RESERVABLE_TYPES = EnumSet.of(
		NotificationType.COUPON_ISSUED,
		NotificationType.NEW_LECTURE_OPENED
	);

	private final Long id;
	private final Long subscriberId;
	private final NotificationType notificationType;
	private final LocalDateTime createdAt;
	private final LocalDateTime reservationTime;
	private boolean isPublished;
	private LocalDateTime updatedAt;
	private final Long version;

	@Builder
	public ReservationInformation(
		Long id,
		Long subscriberId,
		NotificationType notificationType,
		LocalDateTime createdAt,
		LocalDateTime reservationTime,
		boolean isPublished,
		LocalDateTime updatedAt,
		Long version
	) {
		this.id = id;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.createdAt = createdAt;
		this.reservationTime = reservationTime;
		this.isPublished = isPublished;
		this.updatedAt = updatedAt;
		this.version = version;
	}

	public static ReservationInformation of(Long subscriberId, NotificationType notificationType, LocalDateTime reservationTime) {
		validate(notificationType, reservationTime);

		return ReservationInformation.builder()
			.subscriberId(subscriberId)
			.notificationType(notificationType)
			.createdAt(LocalDateTime.now())
			.reservationTime(reservationTime)
			.updatedAt(LocalDateTime.now())
			.isPublished(false)
			.version(0L)
			.build();
	}

	private static void validate(NotificationType notificationType, LocalDateTime reservationTime) {
		if (!RESERVABLE_TYPES.contains(notificationType)) {
			throw new IllegalArgumentException("해당 알림 타입은 예약 발송을 지원하지 않습니다: " + notificationType);
		}

		if (reservationTime.getMinute() != 0 || reservationTime.getSecond() != 0 || reservationTime.getNano() != 0) {
			throw new IllegalArgumentException("예약 시간은 1시간 단위(정각)로만 설정 가능합니다: " + reservationTime);
		}

		if (reservationTime.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("과거 시간으로 예약할 수 없습니다: " + reservationTime);
		}
	}

	public void markAsPublished() {
		this.isPublished = true;
		this.updatedAt = LocalDateTime.now();
	}
}
