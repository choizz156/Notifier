package io.github.choizz.notifier.core.application.dto;

import io.github.choizz.notifier.core.domain.model.NotificationStatus;

public record NotificationStatusResponse(
	Long notificationId,
	NotificationStatus status
) {
}
