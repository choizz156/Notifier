package io.github.choizz.notifier.application.dto;

import java.time.LocalDateTime;

import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.notifier.domain.model.Notification;
import io.github.choizz.notifier.domain.model.NotificationStatus;
import io.github.choizz.notifier.domain.model.NotificationType;

public record NotificationResponse(
	Long id,
	Long subscriberId,
	NotificationType notificationType,
	Channel channel,
	NotificationStatus status,
	String message,
	boolean isRead,
	LocalDateTime createdAt
) {
	public static NotificationResponse from(Notification notification) {
		return new NotificationResponse(
			notification.id(),
			notification.subscriberId(),
			notification.notificationType(),
			notification.channel(),
			notification.status(),
			notification.failMessage(),
			notification.isRead(),
			notification.createdAt()
		);
	}
}
