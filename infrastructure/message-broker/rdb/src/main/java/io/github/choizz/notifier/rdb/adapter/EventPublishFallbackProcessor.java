package io.github.choizz.notifier.rdb.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationFailContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EventPublishFallbackProcessor {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationEventLogUseCase notificationEventLogUseCase;

	public void handle(NotifierPort notifierPort, PublicationFailContext context) {

		NotificationEventLog eventLog = notificationEventLogPersistencePort.findLatestByNotificationId(
			context.publishCommandEvent().notificationId()
		);

		context.increaseRetryCount(eventLog.retryCount());

		notificationEventLogUseCase.recordEventLog(context, EventStatus.RETRIED);

		//TODO: 재발행...
	}
}
