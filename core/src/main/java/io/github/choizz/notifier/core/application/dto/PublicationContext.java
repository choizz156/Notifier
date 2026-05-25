package io.github.choizz.notifier.core.application.dto;

import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class PublicationContext {

	private final Long notificationId;
	private final Long subscriberId;
	private final String notificationType;
	private final String channel;
	private final String metadata;
	private final String failReason;
	private final String referenceType;
	private int retryCount;

	@Builder(toBuilder = true)
	private PublicationContext(
		Long notificationId,
		Long subscriberId,
		String notificationType,
		String channel,
		String metadata,
		String failReason,
		String referenceType,
		int retryCount
	) {

		this.notificationId = notificationId;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.metadata = metadata;
		this.failReason = failReason;
		this.referenceType = referenceType;
		this.retryCount = retryCount;
	}

	public static PublicationContext success(PublishCompletedEvent event) {

		return PublicationContext.builder()
			.notificationId(event.notificationId())
			.notificationType(event.notificationType())
			.channel(event.channel())
			.metadata(event.metadata())
			.referenceType(event.referenceType())
			.retryCount(0)
			.build();
	}

	public PublicationContext notSent(String failReason) {

		return this.toBuilder()
			.failReason(failReason)
			.build();
	}

	public static PublicationContext of(PublishCommandEvent event) {

		return PublicationContext.builder()
			.notificationId(event.notificationId())
			.subscriberId(event.subscriberId())
			.notificationType(event.notificationType())
			.channel(event.channel())
			.metadata(event.metadata())
			.referenceType(event.referenceType())
			.retryCount(0)
			.build();
	}

	public void updateRetryCount(int retryCount) {

		this.retryCount = retryCount + 1;
	}
}
