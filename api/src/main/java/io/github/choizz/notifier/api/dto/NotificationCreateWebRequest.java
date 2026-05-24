package io.github.choizz.notifier.api.dto;

import java.util.Map;

public record NotificationCreateWebRequest(
	Long subscriberId,
	String NotificationType,
	Map<String, String> metadata
) {
}
