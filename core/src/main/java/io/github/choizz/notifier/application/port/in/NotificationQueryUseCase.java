package io.github.choizz.notifier.application.port.in;

import io.github.choizz.notifier.application.dto.NotificationResponse;
import io.github.choizz.notifier.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.application.dto.PageResult;

public interface NotificationQueryUseCase {

	NotificationStatusResponse getStatus(Long notificationId);

	PageResult<NotificationResponse> getNotifications(Long subscriberId, Boolean isRead, int page, int size);
}
