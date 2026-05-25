package io.github.choizz.notifier.core.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode
@Getter
@Accessors(fluent = true)
public class PublicNotificationReceipt {

	private final Long id;
	private final Long subscriberId;
	private final Long publicNotificationId;
	private final LocalDateTime readAt;

	@Builder
	private PublicNotificationReceipt(
		Long id,
		Long subscriberId,
		Long publicNotificationId,
		LocalDateTime readAt
	) {
		this.id = id;
		this.subscriberId = subscriberId;
		this.publicNotificationId = publicNotificationId;
		this.readAt = readAt != null ? readAt : LocalDateTime.now();
	}

	public static PublicNotificationReceipt create(Long subscriberId, Long publicNotificationId) {
		return PublicNotificationReceipt.builder()
			.subscriberId(subscriberId)
			.publicNotificationId(publicNotificationId)
			.build();
	}
}
