package io.github.choizz.notifier.core.application.dto;

import java.util.Map;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.Builder;

@Builder
public record NotificationContext(
	long subscriberId,
	NotificationType notificationType,
	Map<String, String> metadata
) {

	public NotificationContext(Long subscriberId, String notificationType, Map<String, String> metadata) {

		this(
			validateSubscriberId(subscriberId),
			parseNotificationType(notificationType),
			metadata == null ? Map.of() : Map.copyOf(metadata)
		);
	}

	private static long validateSubscriberId(Long subscriberId) {

		if (subscriberId == null) {
			throw new IllegalArgumentException("subscriberId가 필요합니다.");
		}
		return subscriberId;
	}

	private static NotificationType parseNotificationType(String notificationType) {

		if (notificationType == null) {
			throw new IllegalArgumentException("NotificationType가 필요합니다.");
		}
		try {
			return NotificationType.valueOf(notificationType);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("지원하지 않는 알람 타입입니다: " + notificationType);
		}
	}

	public String metadataToJson() {
		return JsonUtils.toJson(this.metadata);
	}
}
