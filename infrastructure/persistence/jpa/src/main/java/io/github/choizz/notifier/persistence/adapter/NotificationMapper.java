package io.github.choizz.notifier.persistence.adapter;

import io.github.choizz.notifier.persistence.entity.NotificationEntity;
import io.github.choizz.notifier.core.domain.model.Notification;

public class NotificationMapper {

	public static NotificationEntity toEntity(Notification notification) {
		NotificationEntity entity = NotificationEntity.builder()
			.subscriberId(notification.subscriberId())
			.notificationType(notification.notificationType())
			.channel(notification.channel())
			.metadata(notification.metadata())
			.status(notification.status())
			.message(notification.failMessage())
			.isRead(notification.isRead())
			.build();
		entity.id(notification.id());
		entity.updatedAt(notification.updatedAt());
		return entity;
	}

	public static Notification toDomain(NotificationEntity entity) {
		return Notification.builder()
			.id(entity.id() != null ? entity.id() : null)
			.subscriberId(entity.subscriberId())
			.notificationType(entity.notificationType())
			.channel(entity.channel())
			.metadata(entity.metadata())
			.status(entity.status())
			.failMessage(entity.message())
			.isRead(entity.isRead())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.build();
	}
}
