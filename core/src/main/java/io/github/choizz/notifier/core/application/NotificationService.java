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
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.application.support.PublishProcessor;
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
	private final PublishProcessor<Long> notificationRetryProcessor;

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

		notificationPersistencePort.markAsRead(notificationId);
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

	@Override
	public void fail(Long notificationId, String failReason) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		notification.markAsFailed(failReason);
		notificationPersistencePort.save(notification);
	}

	@Override
	public void retry() {
		ChunkExecutor.execute(
			0L,
			id -> id,
			lastId -> notificationEventLogUseCase.findUnprocessedNotificationIds(lastId, ChunkExecutor.CHUNK_SIZE),
			this::publish
		);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificationStatusResponse findStatus(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		return new NotificationStatusResponse(notification.id(), notification.status());
	}

	@Override
	@Transactional(readOnly = true)
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

	@Override
	@Transactional(readOnly = true)
	public NotificationDetailResponse getNotificationDetail(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		String content = templateRendererPort.render(
			notification.channel(),
			notification.notificationType(),
			JsonUtils.toMap(notification.metadata())
		);
		return NotificationDetailResponse.of(notification, content);
	}

	private void publish(List<Long> notificationIds) {
		for (Long id : notificationIds) {
			try {
				notificationRetryProcessor.process(id);
			} catch (Exception e) {
				log.warn("알림 재시도 처리 실패: id={}", id, e);
			}
		}
	}
}
