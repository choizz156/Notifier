package io.github.choizz.notifier.persistence.jpa.adapter;

import io.github.choizz.notifier.persistence.jpa.entity.NotificationEntity;
import io.github.choizz.notifier.core.domain.model.Notification;

public class NotificationMapper {

	public static NotificationEntity toEntity(Notification notification) {
		NotificationEntity entity = NotificationEntity.builder()
			.subscriberId(notification.subscriberId())
			.notificationType(notification.notificationType())
			.channel(notification.channel())
			.idempotencyKey(notification.idempotencyKey())
			.metadata(notification.metadata())
			.status(notification.status())
			.message(notification.failMessage())
			.isRead(notification.isRead())
			.recoverCount(notification.recoverCount())
			.build();
		entity.id(notification.id());
		entity.updatedAt(notification.updatedAt());
		entity.version(notification.version());
		return entity;
	}

	public static Notification toDomain(NotificationEntity entity) {
		return Notification.builder()
			.id(entity.id() != null ? entity.id() : null)
			.subscriberId(entity.subscriberId())
			.notificationType(entity.notificationType())
			.channel(entity.channel())
			.idempotencyKey(entity.idempotencyKey())
			.metadata(entity.metadata())
			.status(entity.status())
			.failMessage(entity.message())
			.isRead(entity.isRead())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.recoverCount(entity.recoverCount())
			.version(entity.version())
			.build();
	}
}
