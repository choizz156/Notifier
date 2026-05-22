package io.github.choizz.adapter;

import io.github.choizz.entity.NotificationEventLogEntity;
import io.github.choizz.notifier.domain.model.NotificationEventLog;

public class NotificationEventLogMapper {

	public static NotificationEventLogEntity toEntity(NotificationEventLog eventLog) {
		return NotificationEventLogEntity.builder()
			.notificationId(eventLog.notificationId())
			.eventType(eventLog.eventType())
			.eventStatus(eventLog.eventStatus())
			.failReason(eventLog.failReason())
			.published(eventLog.published())
			.publishedAt(eventLog.publishedAt())
			.build();
	}

	public static NotificationEventLog toDomain(NotificationEventLogEntity entity) {
		return NotificationEventLog.builder()
			.id(entity.id())
			.notificationId(entity.notificationId())
			.eventType(entity.eventType())
			.eventStatus(entity.eventStatus())
			.failReason(entity.failReason())
			.published(entity.published())
			.publishedAt(entity.publishedAt())
			.createdAt(entity.createdAt())
			.build();
	}
}
