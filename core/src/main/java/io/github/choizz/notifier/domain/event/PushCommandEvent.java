package io.github.choizz.notifier.domain.event;

import lombok.Builder;

@Builder
public record PushCommandEvent(
	long notificationId,
	long subscriberId,
	String notificationType,
	String channel,
	String metadata
) {

	public static PushCommandEvent of(NotificationRequestedEvent event, String metadata) {

		return PushCommandEvent.builder()
			.channel(event.channel())
			.metadata(metadata)
			.notificationType(event.notificationType())
			.notificationId(event.notificationId())
			.subscriberId(event.subscriberId())
			.build();
	}

}
