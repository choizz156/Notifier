package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;

public interface LoadCombinedNotificationPort {
	PageResult<NotificationResponse> loadCombinedNotifications(Long subscriberId, Boolean isRead, int page, int size);
}
