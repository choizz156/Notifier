package io.github.choizz.notifier.core.application.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRetryProcessor {

	private final NotificationPersistencePort notificationPersistencePort;
	private final ApplicationEventPublisher applicationEventPublisher;


	@Transactional
	public void processChunk(List<Long> notificationIds) {

		List<Notification> notifications = notificationPersistencePort.findAllByIds(notificationIds);
		List<Notification> successfulNotifications = new ArrayList<>();

		for (Notification notification : notifications) {
			try {
				notification.markAsPendingForRecover();

				NotificationContext context = new NotificationContext(
					notification.subscriberId(),
					notification.notificationType().name(),
					JsonUtils.toMap(notification.metadata())
				);
				applicationEventPublisher.publishEvent(NotificationRequestedEvent.of(notification, context));
				successfulNotifications.add(notification);
			} catch (Exception e) {
				log.warn("알림 재시도 처리 실패: id={}", notification.id(), e);
			}
		}

		if (!successfulNotifications.isEmpty()) {
			notificationPersistencePort.saveAll(successfulNotifications);
		}
	}
}
