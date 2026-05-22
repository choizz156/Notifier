package io.github.choizz.notifier.domain.event;

import java.util.Map;

import io.github.choizz.notifier.application.dto.AlarmContext;
import lombok.Builder;

@Builder
public record AlarmRequestedEvent(
	long subscriberId,
	String alarmType,
	String channel,
	Map<String, String> metadata
) {

	public static AlarmRequestedEvent of(AlarmContext alarmContext) {
		return AlarmRequestedEvent.builder()
			.channel(alarmContext.channel().name())
			.alarmType(alarmContext.alarmType().name())
			.subscriberId(alarmContext.subscriberId())
			.metadata(alarmContext.metadata())
			.build();
	}
}
