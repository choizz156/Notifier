package io.github.choizz.notifier.core.domain.event;

import java.util.Map;

import lombok.Builder;

@Builder
public record PublishCommandEvent(
	Long notificationId,
	Long subscriberId,
	String notificationType,
	String channel,
	Map<String, String> metadata
) {

	public static PublishCommandEvent of(NotificationRequestedEvent event, Map<String, String> metadata) {

		return PublishCommandEvent.builder()
			.channel(event.channel())
			.metadata(Map.copyOf(metadata))
			.notificationType(event.notificationType())
			.notificationId(event.notificationId())
			.subscriberId(event.subscriberId())
			.build();
	}
}
