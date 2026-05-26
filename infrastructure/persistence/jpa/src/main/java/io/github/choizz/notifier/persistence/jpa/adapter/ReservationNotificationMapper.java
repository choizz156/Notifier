package io.github.choizz.notifier.persistence.jpa.adapter;

import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import io.github.choizz.notifier.persistence.jpa.entity.ReservationNotificationEntity;

public class ReservationNotificationMapper {

	public static ReservationNotificationEntity toEntity(ReservationInformation domain) {
		ReservationNotificationEntity entity = ReservationNotificationEntity.builder()
			.subscriberId(domain.subscriberId())
			.notificationType(domain.notificationType())
			.reservationTime(domain.reservationTime())
			.isPublished(domain.isPublished())
			.build();

		if (domain.id() != null) {
			entity.id(domain.id());
		}
		if (domain.updatedAt() != null) {
			entity.updatedAt(domain.updatedAt());
		}
		if (domain.version() != null) {
			entity.version(domain.version());
		}

		return entity;
	}

	public static ReservationInformation toDomain(ReservationNotificationEntity entity) {
		return ReservationInformation.builder()
			.id(entity.id())
			.subscriberId(entity.subscriberId())
			.notificationType(entity.notificationType())
			.reservationTime(entity.reservationTime())
			.isPublished(entity.isPublished())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.version(entity.version())
			.build();
	}
}
