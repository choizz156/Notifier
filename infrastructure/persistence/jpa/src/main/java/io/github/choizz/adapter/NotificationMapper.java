package io.github.choizz.adapter;

import io.github.choizz.entity.NotificationEntity;
import io.github.choizz.notifier.domain.model.Notification;

public class NotificationMapper {

	public static NotificationEntity toEntity(Notification notification) {
		return NotificationEntity.builder()
			.subscriberId(notification.subscriberId())
			.notificationType(notification.notificationType())
			.channel(notification.channel())
			.metadata(notification.metadata())
			.status(notification.status())
			.message(notification.message())
			.build();
	}

	public static Notification toDomain(NotificationEntity entity) {
		return Notification.builder()
			.id(entity.id() != null ? Long.valueOf(entity.id()) : null)
			.subscriberId(entity.subscriberId())
			.notificationType(entity.notificationType())
			.channel(entity.channel())
			.metadata(entity.metadata())
			.status(entity.status())
			.message(entity.message())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.build();
	}
}
