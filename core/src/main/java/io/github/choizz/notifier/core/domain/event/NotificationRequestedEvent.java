package io.github.choizz.notifier.core.domain.event;

import static io.github.choizz.notifier.core.domain.model.ReferenceType.*;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.domain.model.Notification;
import lombok.Builder;

@Builder
public record NotificationRequestedEvent(
	Long notificationId,
	Long subscriberId,
	String notificationType,
	String channel,
	String metadata,
	String referenceType
) {

	public static NotificationRequestedEvent of(Notification notification, NotificationContext context) {
		return NotificationRequestedEvent.builder()
			.notificationId(notification.id())
			.subscriberId(context.subscriberId())
			.notificationType(context.notificationType().name())
			.channel(notification.channel().name())
			.metadata(notification.metadata())
			.referenceType(PERSONAL.name())
			.build();
	}
}

