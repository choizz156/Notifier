package io.github.choizz.notifier.core.domain.event;

import java.util.Map;

import lombok.Builder;

@Builder
public record PublishCommandEvent(
	long notificationId,
	long subscriberId,
	String notificationType,
	String channel,
	Map<String, String> metadata
) {

	public static PublishCommandEvent of(NotificationRequestedEvent event, Map<String, String> metadata) {

		return PublishCommandEvent.builder()
			.channel(event.channel())
			.metadata(metadata)
			.notificationType(event.notificationType())
			.notificationId(event.notificationId())
			.subscriberId(event.subscriberId())
			.build();
	}
}
