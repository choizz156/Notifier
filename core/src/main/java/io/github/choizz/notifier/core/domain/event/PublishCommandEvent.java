package io.github.choizz.notifier.core.domain.event;

import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import lombok.Builder;

@Builder
public record PublishCommandEvent(
	Long referencedId,
	Long subscriberId,
	String notificationType,
	String channel,
	String metadata,
	String referenceType
) {

	public static PublishCommandEvent of(NotificationRequestedEvent event) {

		return PublishCommandEvent.builder()
			.channel(event.channel())
			.metadata(event.metadata())
			.notificationType(event.notificationType())
			.referencedId(event.notificationId())
			.subscriberId(event.subscriberId())
			.referenceType(event.referenceType())
			.build();
	}

	public static PublishCommandEvent toPublic(
		PublicNotification publicNotification,
		Long subscriberId,
		String channel
		) {

		return PublishCommandEvent.builder()
			.channel(channel)
			.metadata(publicNotification.metadata())
			.notificationType(String.valueOf(publicNotification.notificationType()))
			.referencedId(publicNotification.id())
			.subscriberId(subscriberId)
			.referenceType(ReferenceType.PUBLIC.name())
			.build();
	}
}
