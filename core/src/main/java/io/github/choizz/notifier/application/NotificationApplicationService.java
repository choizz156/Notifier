package io.github.choizz.notifier.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.application.dto.NotificationContext;
import io.github.choizz.notifier.application.port.in.NotificationPushUseCase;
import io.github.choizz.notifier.application.port.in.NotificationReadUseCase;
import io.github.choizz.notifier.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationApplicationService implements NotificationPushUseCase, NotificationReadUseCase {

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
}
