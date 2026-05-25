package io.github.choizz.notifier.core.domain.event;

import static io.github.choizz.notifier.core.domain.model.ReferenceType.*;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import lombok.Builder;

@Builder
public record PublicNotificationRequestedEvent(
	Long publicNotificationId,
	Long subscriberId,
	String notificationType,
	String metadata,
	String referenceType,
	String channel
) {

	public static PublicNotificationRequestedEvent of(
		PublicNotification publicNotification,
		NotificationContext context,
		String channel,
		Long subscriberId
	) {

		return PublicNotificationRequestedEvent.builder()
			.publicNotificationId(publicNotification.id())
			.subscriberId(subscriberId)
			.notificationType(context.notificationType().name())
			.metadata(publicNotification.metadata())
			.referenceType(PUBLIC.name())
			.channel(channel)
			.build();
	}
}
