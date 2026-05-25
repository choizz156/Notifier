package io.github.choizz.notifier.persistence.adapter;

import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;
import io.github.choizz.notifier.core.domain.model.NotificationLog;

public class NotificationLogMapper {

	public static NotificationEventLogEntity toEntity(NotificationLog eventLog) {

		NotificationEventLogEntity entity = NotificationEventLogEntity.builder()
			.notificationId(eventLog.notificationId())
			.notificationType(eventLog.notificationType())
			.channelType(eventLog.channelType())
			.eventStatus(eventLog.eventStatus())
			.failReason(eventLog.failReason())
			.retryCount(eventLog.retryCount())
			.published(eventLog.published())
			.publishedAt(eventLog.publishedAt())
			.build();

		entity.id(eventLog.id());
		entity.updatedAt(eventLog.updatedAt());
		return entity;
	}

	public static NotificationLog toDomain(NotificationEventLogEntity entity) {
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
