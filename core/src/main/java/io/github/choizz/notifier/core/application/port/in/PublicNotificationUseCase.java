package io.github.choizz.notifier.core.application.port.in;

public interface PublicNotificationUseCase {
	void markAsRead(Long subscriberId, Long publicNotificationId);
}
