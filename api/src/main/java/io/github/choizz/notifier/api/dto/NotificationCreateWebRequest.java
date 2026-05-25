package io.github.choizz.notifier.api.dto;

import java.util.Map;

public record NotificationCreateWebRequest(
	Long subscriberId,
	String notificationType,
	Map<String, String> metadata
) {
}
