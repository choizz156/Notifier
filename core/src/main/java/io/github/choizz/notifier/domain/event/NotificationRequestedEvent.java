package io.github.choizz.notifier.domain.event;

import java.util.Map;

import io.github.choizz.notifier.application.dto.NotificationContext;
import io.github.choizz.notifier.domain.model.Notification;
import lombok.Builder;

@Builder
public record NotificationRequestedEvent(
	long notificationId,
	long subscriberId,
	String notificationType,
	String channel,
	Map<String, String> metadata
) {

	public static NotificationRequestedEvent of(Notification notification, NotificationContext context) {
		return NotificationRequestedEvent.builder()
			.notificationId(notification.id())
			.subscriberId(context.subscriberId())
			.notificationType(context.notificationType().name())
			.channel(context.channel().name())
			.metadata(context.metadata())
			.build();
	}
}

