package io.github.choizz.notifier.core.application.port.in;

public interface NotificationReadUseCase {

	void markAsRead(Long notificationId);
}
