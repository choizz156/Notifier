package io.github.choizz.notifier.application.port.in;

public interface NotificationReadUseCase {

	void markAsRead(Long notificationId);
}
