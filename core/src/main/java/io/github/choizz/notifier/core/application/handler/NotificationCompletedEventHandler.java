package io.github.choizz.notifier.core.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationCompletedEventHandler {

	private final NotificationEventLogUseCase notificationEventLogUseCase;
	private final NotificationUseCase notificationUseCase;

	@Async("taskExecutor")
	@EventListener
	public void updateNotification(PublishCompletedEvent event) {
		notificationUseCase.updateStatus(event.notificationId(), NotificationStatus.COMPLETED);
	}

	@Async("taskExecutor")
	@EventListener
	public void updateNotificationLog(PublishCompletedEvent event) {
		notificationEventLogUseCase.done(event.notificationId(), EventStatus.SENT);
	}
}
