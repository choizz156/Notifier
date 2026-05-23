package io.github.choizz.notifier.core.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService implements NotificationUseCase {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final NotificationPersistencePort NotificationPersistencePort;

	@Override
	public void push(NotificationContext NotificationContext) {

		boolean isDuplicate = NotificationPersistencePort.existsDuplicate(
			NotificationContext.subscriberId(),
			NotificationContext.notificationType(),
			NotificationContext.channel()
		);

		if (isDuplicate) {
			throw new IllegalStateException("이미 처리 중인 동일한 알람이 존재합니다. id = %s, type = %s, channel = %s"
					.formatted(NotificationContext.subscriberId(), NotificationContext.notificationType(), NotificationContext.channel())
			);
		}

		Notification notification = Notification.from(NotificationContext);
		Notification savedNotification = NotificationPersistencePort.save(notification);

		applicationEventPublisher.publishEvent(NotificationRequestedEvent.of(savedNotification, NotificationContext));
	}

	@Override
	public void markAsRead(Long notificationId) {
		Notification notification = NotificationPersistencePort.findById(notificationId);
		notification.markAsRead();
		NotificationPersistencePort.save(notification);
	}

	@Override
	public void updateStatus(Long notificationId, NotificationStatus status) {
		Notification notification = NotificationPersistencePort.findById(notificationId);
		
		switch (status) {
			case COMPLETED -> notification.markAsCompleted();
			case FAILED -> notification.markAsFailed();
			case RETRYING -> notification.markAsRetrying();
			case SENDING -> notification.markAsSending();
		}
		
		NotificationPersistencePort.save(notification);
	}

	@Transactional(readOnly = true)
	@Override
	public NotificationStatusResponse getStatus(Long notificationId) {
		Notification notification = NotificationPersistencePort.findById(notificationId);
		return new NotificationStatusResponse(notification.id(), notification.status());
	}

	@Transactional(readOnly = true)
	@Override
	public PageResult<NotificationResponse> getNotifications(Long subscriberId, Boolean isRead, int page, int size) {
		PageResult<Notification> pageResult = NotificationPersistencePort.findAllBySubscriberId(subscriberId, isRead, page, size);
		
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
}
