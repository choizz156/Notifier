package io.github.choizz.notifier.domain.model;

import java.time.LocalDateTime;

import io.github.choizz.notifier.application.dto.AlarmContext;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode
@Getter
@Accessors(fluent = true)
public class Alarm {

	private final Long id;
	private final Long subscriberId;
	private final AlarmType alarmType;
	private final Channel channel;
	private final String metadata;
	private AlarmStatus status;
	private String message;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	private Alarm(
		Long id,
		Long subscriberId,
		AlarmType alarmType,
		Channel channel,
		String metadata,
		AlarmStatus status,
		String message,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {

		this.id = id;
		this.subscriberId = subscriberId;
		this.alarmType = alarmType;
		this.channel = channel;
		this.metadata = metadata;
		this.status = status != null ? status : AlarmStatus.PENDING;
		this.message = message;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
	}

	public static Alarm from(AlarmContext context) {
		String message = AlarmMessage.generate(context.alarmType(), context.channel(), context.metadata());
		return Alarm.builder()
			.subscriberId(context.subscriberId())
			.alarmType(context.alarmType())
			.channel(context.channel())
			.status(AlarmStatus.PENDING)
			.message(message)
			.metadata(context.metadataToJson())
			.build();
	}

	public void markAsCompleted() {
		this.status = AlarmStatus.COMPLETED;
	}

	public void markAsFailed() {
		this.status = AlarmStatus.FAILED;
	}

	public void markAsRetrying() {
		this.status = AlarmStatus.RETRYING;
	}

	public void markAsSending() {
		this.status = AlarmStatus.SENDING;
	}

}
