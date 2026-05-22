package io.github.choizz.notifier.application.dto;

import java.util.Map;

import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.notifier.domain.model.NotificationType;
import io.github.choizz.notifier.domain.util.JsonUtils;
import lombok.Builder;

@Builder
public record NotificationContext(
	long subscriberId,
	NotificationType notificationType,
	Channel channel,
	Map<String, String> metadata
) {

	public NotificationContext(Long subscriberId, String NotificationType, String channel, Map<String, String> metadata) {

		this(
			validateSubscriberId(subscriberId),
			parseNotificationType(NotificationType),
			parseChannel(channel),
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

	private static Channel parseChannel(String channel) {

		if (channel == null) {
			throw new IllegalArgumentException("channel이 필요합니다.");
		}
		try {
			return Channel.valueOf(channel.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("지원하지 않는 채널입니다: " + channel);
		}
	}
}
