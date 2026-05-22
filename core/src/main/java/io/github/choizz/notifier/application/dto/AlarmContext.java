package io.github.choizz.notifier.application.dto;

import io.github.choizz.notifier.domain.model.AlarmType;
import io.github.choizz.notifier.domain.model.Channel;

import java.util.Map;

public record AlarmContext(
	long subscriberId,
	AlarmType alarmType,
	Channel channel,
	Map<String, String> metadata
) {

	public AlarmContext(Long subscriberId, String alarmType, String channel, Map<String, String> metadata) {
		this(
			validateSubscriberId(subscriberId),
            parseAlarmType(alarmType),
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

	private static AlarmType parseAlarmType(String alarmType) {
		if (alarmType == null) {
			throw new IllegalArgumentException("alarmType가 필요합니다.");
		}
		try {
			return AlarmType.valueOf(alarmType.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("지원하지 않는 알람 타입입니다: " + alarmType);
		}
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
