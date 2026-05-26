package io.github.choizz.notifier.core.application.port.in;

import io.github.choizz.notifier.core.application.dto.NotificationContext;

public interface PublicNotificationUseCase {
	void markAsRead(Long subscriberId, Long publicNotificationId);

	void pushToPublic(NotificationContext context);

	void completeIfAllDone(Long publicNotificationId);
}
