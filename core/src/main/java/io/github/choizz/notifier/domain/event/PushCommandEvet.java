package io.github.choizz.notifier.domain.event;

import java.util.Map;

public record PushCommandEvet(
	long notificationId,
	long subscriberId,
	String notificationType,
	String channel,
	Map<String, String> metadata
) {

}
