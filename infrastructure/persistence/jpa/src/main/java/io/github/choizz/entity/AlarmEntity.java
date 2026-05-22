package io.github.choizz.entity;

import io.github.choizz.notifier.domain.model.AlarmStatus;
import io.github.choizz.notifier.domain.model.AlarmType;
import io.github.choizz.notifier.domain.model.Channel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "alarms")
@Getter
@NoArgsConstructor
@Accessors(fluent = true)
public class AlarmEntity extends BaseEntity {

	@Column(nullable = false)
	private Long subscriberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlarmType alarmType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Channel channel;

	@Column(columnDefinition = "json")
	private String metadata;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlarmStatus status;

	private String message;

	@Builder
	public AlarmEntity(
		Long subscriberId,
		AlarmType alarmType,
		Channel channel,
		String metadata,
		AlarmStatus status,
		String message
	) {

		this.subscriberId = subscriberId;
		this.alarmType = alarmType;
		this.channel = channel;
		this.metadata = metadata;
		this.status = status;
		this.message = message;
	}
}
