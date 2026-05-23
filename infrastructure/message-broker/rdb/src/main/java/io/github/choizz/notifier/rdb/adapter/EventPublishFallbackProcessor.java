package io.github.choizz.notifier.rdb.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EventPublishFallbackProcessor {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationLogUseCase notificationLogUseCase;

	public void handle(NotifierPort notifierPort, long notificationId) {

		NotificationEventLog eventLog =
			notificationEventLogPersistencePort.findLatestByNotificationId(notificationId);

		 notificationLogUseCase.updateStatus(eventLog, EventStatus.RETRIED);


		//TODO: 재발행...
	}
}
