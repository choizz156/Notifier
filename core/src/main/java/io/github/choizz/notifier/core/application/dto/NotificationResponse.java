package io.github.choizz.notifier.core.application.dto;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

public record NotificationResponse(
	Long id,
	ReferenceType referenceType,
	Long subscriberId,
	NotificationType notificationType,
	Channel channel,
	NotificationStatus status,
	String title,
	boolean isRead,
	LocalDateTime createdAt,
	int recoverCount
) {
	public static NotificationResponse from(Notification notification) {
		return new NotificationResponse(
			notification.id(),
			ReferenceType.PERSONAL,
			notification.subscriberId(),
			notification.notificationType(),
			notification.channel(),
			notification.status(),
			notification.notificationType().title(),
			notification.isRead(),
			notification.createdAt(),
			notification.recoverCount()
		);
	}
}
