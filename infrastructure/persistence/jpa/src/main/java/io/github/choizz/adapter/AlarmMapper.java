package io.github.choizz.adapter;

import io.github.choizz.entity.AlarmEntity;
import io.github.choizz.notifier.domain.model.Alarm;

public class AlarmMapper {

	public static AlarmEntity toEntity(Alarm alarm) {
		return AlarmEntity.builder()
			.subscriberId(alarm.subscriberId())
			.alarmType(alarm.alarmType())
			.channel(alarm.channel())
			.metadata(alarm.metadata())
			.status(alarm.status())
			.message(alarm.message())
			.build();
	}

	public static Alarm toDomain(AlarmEntity entity) {
		return Alarm.builder()
			.id(entity.id() != null ? Long.valueOf(entity.id()) : null)
			.subscriberId(entity.subscriberId())
			.alarmType(entity.alarmType())
			.channel(entity.channel())
			.metadata(entity.metadata())
			.status(entity.status())
			.message(entity.message())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.build();
	}
}
