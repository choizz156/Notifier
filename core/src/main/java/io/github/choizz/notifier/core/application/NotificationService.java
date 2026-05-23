package io.github.choizz.notifier.core.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.dto.NotificationDetailResponse;
import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService implements NotificationUseCase {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final NotificationPersistencePort notificationPersistencePort;
	private final NotificationEventLogUseCase notificationEventLogUseCase;
	private final TemplateRendererPort templateRendererPort;

	@Override
	public void push(NotificationContext NotificationContext) {

		boolean isDuplicate = notificationPersistencePort.existsDuplicate(
			NotificationContext.subscriberId(),
			NotificationContext.notificationType(),
			NotificationContext.channel()
		);

		if (isDuplicate) {
			throw new IllegalStateException("이미 처리 중인 동일한 알람이 존재합니다. id = %s, type = %s, channel = %s"
				.formatted(NotificationContext.subscriberId(), NotificationContext.notificationType(),
					NotificationContext.channel())
			);
		}

		Notification notification = Notification.from(NotificationContext);
		Notification savedNotification = notificationPersistencePort.save(notification);

		applicationEventPublisher.publishEvent(NotificationRequestedEvent.of(savedNotification, NotificationContext));
	}

	@Override
	public void markAsRead(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		notification.markAsRead();
		notificationPersistencePort.save(notification);
	}

	@Override
	public void updateStatus(Long notificationId, NotificationStatus status) {

		Notification notification = notificationPersistencePort.findById(notificationId);

		switch (status) {
			case COMPLETED -> notification.markAsCompleted();
			case RETRYING -> notification.markAsRetrying();
		}

		notificationPersistencePort.save(notification);
	}

	@Transactional(readOnly = true)
	@Override
	public NotificationStatusResponse getStatus(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		return new NotificationStatusResponse(notification.id(), notification.status());
	}

	@Transactional(readOnly = true)
	@Override
	public PageResult<NotificationResponse> getNotifications(Long subscriberId, Boolean isRead, int page, int size) {

		PageResult<Notification> pageResult = notificationPersistencePort.findAllBySubscriberId(subscriberId, isRead,
			page, size);

		List<NotificationResponse> responses = pageResult.content().stream()
			.map(NotificationResponse::from)
			.toList();

		return new PageResult<>(
			responses,
			pageResult.page(),
			pageResult.size(),
			pageResult.totalElements(),
			pageResult.totalPages()
		);
	}

	@Transactional(readOnly = true)
	@Override
	public NotificationDetailResponse getNotificationDetail(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		String content = templateRendererPort.render(
			notification.channel(),
			notification.notificationType(),
			JsonUtils.toMap(notification.metadata())
		);
		return NotificationDetailResponse.of(notification, content);
	}

	@Override
	public void fail(Long notificationId, String failReason) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		notification.markAsFailed(failReason);
		notificationPersistencePort.save(notification);
	}

	@Override
	public void retry() {

		long lastId = 0L;
		int chunkSize = 500;

		while (true) {
			List<Long> notificationIds = notificationEventLogUseCase.findUnprocessedNotificationIds(lastId, chunkSize);

			if (notificationIds.isEmpty()) {
				log.info("수동 알림 재시도 완료");
				return;
			}

			for (Long id : notificationIds) {
				process(id);
			}

			lastId = notificationIds.getLast();
		}
	}

	private void process(Long id) {

		Notification notification = notificationPersistencePort.findById(id);
		notification.markAsPendingForManualRetry();
		Notification savedNotification = notificationPersistencePort.save(notification);

		NotificationContext context = new NotificationContext(
			savedNotification.subscriberId(),
			savedNotification.notificationType().name(),
			savedNotification.channel().name(),
			JsonUtils.toMap(savedNotification.metadata())
		);
		applicationEventPublisher.publishEvent(NotificationRequestedEvent.of(savedNotification, context));
	}
}
