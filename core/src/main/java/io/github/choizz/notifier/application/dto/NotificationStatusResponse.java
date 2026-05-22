package io.github.choizz.notifier.application.dto;

import io.github.choizz.notifier.domain.model.NotificationStatus;

public record NotificationStatusResponse(
	Long notificationId,
	NotificationStatus status
) {
}
