package io.github.choizz.notifier.core.domain.event;

import io.github.choizz.notifier.core.application.dto.PublicationContext;

public record PublishCompletedEvent(
	long notificationId,
	String notificationType,
	String channel,
	String metadata
) {

	public PublishCompletedEvent(PublicationContext context) {
		this(context.notificationId(), context.notificationType(), context.channel(), context.metadata());
	}
}
