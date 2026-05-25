package io.github.choizz.notifier.persistence.adapter;

import io.github.choizz.notifier.persistence.entity.NotificationLogEntity;
import io.github.choizz.notifier.core.domain.model.NotificationLog;

public class NotificationLogMapper {

	public static NotificationLogEntity toEntity(NotificationLog notificationLog) {

		NotificationLogEntity entity = NotificationLogEntity.builder()
			.notificationId(notificationLog.notificationId())
			.notificationType(notificationLog.notificationType())
			.channelType(notificationLog.channelType())
			.eventStatus(notificationLog.eventStatus())
			.failReason(notificationLog.failReason())
			.retryCount(notificationLog.retryCount())
			.published(notificationLog.published())
			.publishedAt(notificationLog.publishedAt())
			.build();

		entity.id(notificationLog.id());
		entity.updatedAt(notificationLog.updatedAt());
		return entity;
	}

	public static NotificationLog toDomain(NotificationLogEntity entity) {
		return NotificationLog.builder()
			.id(entity.id())
			.notificationId(entity.notificationId())
			.notificationType(entity.notificationType())
			.channelType(entity.channelType())
			.eventStatus(entity.eventStatus())
			.failReason(entity.failReason())
			.retryCount(entity.retryCount())
			.published(entity.published())
			.publishedAt(entity.publishedAt())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.build();
	}
}
