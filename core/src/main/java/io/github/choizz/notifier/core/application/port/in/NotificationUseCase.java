package io.github.choizz.notifier.core.application.port.in;

import java.util.List;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.dto.NotificationDetailResponse;
import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;

public interface NotificationUseCase {

	void push(NotificationContext notificationContext);

	void markAsRead(Long notificationId);

	void updateStatus(Long notificationId, NotificationStatus status);

	NotificationStatusResponse findStatus(Long notificationId);

	PageResult<NotificationResponse> findNotifications(Long subscriberId, Boolean isRead, int page, int size);

	NotificationDetailResponse findNotificationDetail(Long notificationId);
	
	void retry();

	void retryStuckNotification(List<Long> notificationId);
	
	void fail(Long notificationId, String failReason );
}
