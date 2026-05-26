package io.github.choizz.notifier.core.domain.model;

import java.time.LocalDateTime;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@EqualsAndHashCode
@Getter
@Accessors(fluent = true)
public class NotificationLog {

	private final Long id;
	private final Long referenceId;
	private final ReferenceType referenceType;
	private final Long subscriberId;
	private final NotificationType notificationType;
	private final Channel channelType;
	private final LocalDateTime createdAt;
	private final String metadata;
	private int retryCount;
	private boolean published;
	private EventStatus eventStatus;
	private String failReason;
	private LocalDateTime publishedAt;
	private LocalDateTime updatedAt;
	private final Long version;

	@Builder
	private NotificationLog(
		Long id,
		Long referenceId,
		ReferenceType referenceType,
		Long subscriberId,
		NotificationType notificationType,
		Channel channelType,
		EventStatus eventStatus,
		String failReason,
		int retryCount,
		boolean published,
		LocalDateTime publishedAt,
		LocalDateTime createdAt,
		String metadata,
		LocalDateTime updatedAt,
		Long version
	) {

		this.id = id;
		this.referenceId = referenceId;
		this.referenceType = referenceType;
		this.subscriberId = subscriberId;
		this.notificationType = notificationType;
		this.channelType = channelType;
		this.eventStatus = eventStatus;
		this.failReason = failReason;
		this.retryCount = retryCount;
		this.published = published;
		this.publishedAt = publishedAt;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.metadata = metadata;
		this.updatedAt = updatedAt;
		this.version = version;
	}

	public static NotificationLog request(Notification notification) {

		return NotificationLog.builder()
			.referenceId(notification.id())
			.referenceType((ReferenceType.PERSONAL))
			.subscriberId(notification.subscriberId())
			.notificationType(notification.notificationType())
			.channelType(notification.channel())
			.eventStatus(EventStatus.REQUESTED)
			.retryCount(0)
			.published(false)
			.metadata(notification.metadata())
			.version(0L)
			.build();
	}

	public static NotificationLog requestToPublic(PublicNotification publicNotification, Channel channel, Long subscriberId) {

		return NotificationLog.builder()
			.referenceId(publicNotification.id())
			.referenceType(ReferenceType.PUBLIC)
			.subscriberId(subscriberId)
			.notificationType(publicNotification.notificationType())
			.channelType(channel)
			.eventStatus(EventStatus.REQUESTED)
			.retryCount(0)
			.published(false)
			.metadata(publicNotification.metadata())
			.version(0L)
			.build();
	}

	public static NotificationLog retried(PublicationContext context) {

		return NotificationLog.builder()
			.referenceId(context.notificationId())
			.referenceType(ReferenceType.valueOf(context.referenceType()))
			.subscriberId(context.subscriberId())
			.notificationType(NotificationType.valueOf(context.notificationType()))
			.channelType(Channel.of(context.channel()))
			.eventStatus(EventStatus.RETRIED)
			.failReason(context.failReason())
			.retryCount(context.retryCount())
			.metadata(context.metadata())
			.published(false)
			.updatedAt(LocalDateTime.now())
			.version(0L)
			.build();
	}

	public static NotificationLog sent(PublicationContext context) {

		return NotificationLog.builder()
			.referenceId(context.notificationId())
			.referenceType(ReferenceType.valueOf(context.referenceType()))
			.subscriberId(context.subscriberId())
			.notificationType(NotificationType.valueOf(context.notificationType()))
			.channelType(Channel.of(context.channel()))
			.eventStatus(EventStatus.SENT)
			.retryCount(context.retryCount())
			.metadata(context.metadata())
			.published(true)
			.publishedAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.version(0L)
			.build();
	}

	public static NotificationLog failed(PublicationContext context) {

		return NotificationLog.builder()
			.referenceId(context.notificationId())
			.referenceType(ReferenceType.valueOf(context.referenceType()))
			.subscriberId(context.subscriberId())
			.notificationType(NotificationType.valueOf(context.notificationType()))
			.channelType(Channel.of(context.channel()))
			.eventStatus(EventStatus.FAILED)
			.failReason(context.failReason())
			.retryCount(context.retryCount())
			.metadata(context.metadata())
			.published(false)
			.updatedAt(LocalDateTime.now())
			.version(0L)
			.build();
	}

	public void markAsProcessing() {
		this.eventStatus = EventStatus.PROCESSING;
		this.updatedAt = LocalDateTime.now();
	}

	public void markAsRetried(String reason, int newRetryCount) {
		this.eventStatus = EventStatus.RETRIED;
		this.failReason = reason;
		this.retryCount = newRetryCount;
		this.updatedAt = LocalDateTime.now();
	}

	public void markAsFailed(String reason, int newRetryCount) {
		this.eventStatus = EventStatus.FAILED;
		this.failReason = reason;
		this.retryCount = newRetryCount;
		this.updatedAt = LocalDateTime.now();
	}
}
