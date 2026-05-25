package io.github.choizz.notifier.core.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationFailedEventHandler {

	private final NotificationUseCase notificationUseCase;
	private final NotificationLogUseCase notificationLogUseCase;

	@Async("taskExecutor")
	@Transactional
	@EventListener
	public void updateNotification(PublishFailedEvent event) {

		notificationLogUseCase.saveNotificationLog(
			event.context().notificationId(),
			EventStatus.FAILED,
			event.context()
		);

		if (ReferenceType.PERSONAL == ReferenceType.valueOf(event.context().referenceType())) {
			notificationUseCase.fail(event.context().notificationId(), event.context().failReason());
		}
	}
}
