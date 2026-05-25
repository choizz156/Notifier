package io.github.choizz.notifier.core.domain.event;

import java.util.List;

public record PublicNotificationRequestedEvent(
	List<Long> subscriberIds,
	String metadata,
	String notificationType,
	String idempotentKey
) {
}
