package io.github.choizz.notifier.core.application.dto;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public record NotificationResponse(
	Long id,
	Long subscriberId,
	NotificationType notificationType,
	Channel channel,
	NotificationStatus status,
	String title,
	boolean isRead,
	LocalDateTime createdAt,
	int manualRetryCount
) {
	public static NotificationResponse from(Notification notification) {
		return new NotificationResponse(
			notification.id(),
			notification.subscriberId(),
			notification.notificationType(),
			notification.channel(),
			notification.status(),
			notification.notificationType().getTitle(),
			notification.isRead(),
			notification.createdAt(),
			notification.manualRetryCount()
		);
	}
}
