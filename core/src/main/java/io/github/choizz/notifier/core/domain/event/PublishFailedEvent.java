package io.github.choizz.notifier.core.domain.event;

public record PublishFailedEvent(
	Long notificationId,
	String failReason
) {
}
