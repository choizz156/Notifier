package io.github.choizz.notifier.core.application.dto;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public record NotificationDetailResponse(
	Long id,
	Long subscriberId,
	NotificationType notificationType,
	Channel channel,
	NotificationStatus status,
	String title,
	String content,
	boolean isRead,
	LocalDateTime createdAt,
	int manualRetryCount
) {
	public static NotificationDetailResponse of(Notification notification, String content) {
		return new NotificationDetailResponse(
			notification.id(),
			notification.subscriberId(),
			notification.notificationType(),
			notification.channel(),
			notification.status(),
			notification.notificationType().title(),
			content,
			notification.isRead(),
			notification.createdAt(),
			notification.recoverCount()
		);
	}
}
