package io.github.choizz.notifier.core.domain.event;

import lombok.Builder;

@Builder
public record PublishCommandEvent(
	Long notificationId,
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
			.notificationId(event.notificationId())
			.subscriberId(event.subscriberId())
			.referenceType(event.referenceType())
			.build();
	}
}
