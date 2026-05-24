package io.github.choizz.notifier.core.application.support;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
public class NotificationRetryProcessor implements PublishProcessor<Long> {

	private final NotificationPersistencePort notificationPersistencePort;
	private final ApplicationEventPublisher applicationEventPublisher;


	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void process(Long id) {

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
