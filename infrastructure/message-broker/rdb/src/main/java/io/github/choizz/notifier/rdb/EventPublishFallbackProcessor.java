package io.github.choizz.notifier.rdb;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.infrastructure.messagebroker.NotificationDispatcher;
import io.github.choizz.notifier.infrastructure.messagebroker.retry.RetryPolicy;
import io.github.choizz.notifier.infrastructure.messagebroker.retry.StandardRetryPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EventPublishFallbackProcessor {

	private final List<RetryPolicy> retryPolicies;
	private final NotificationDispatcher notificationDispatcher;
	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationEventLogUseCase notificationEventLogUseCase;

	public void handle(NotifierPort notifierPort, PublicationContext context) {

		RetryPolicy retryPolicy = findRetryPolicy(NotificationType.valueOf(context.notificationType()));

		NotificationEventLog eventLog = notificationEventLogPersistencePort.findRetryingEventLogByNotificationId(
			context.notificationId()
		);

		if(context.retryCount() >= retryPolicy.getMaxRetryCount()){
			notificationEventLogUseCase.saveEventLog(context.notificationId(), EventStatus.FAILED, context);
			return;
		}
		context.increaseRetryCount(eventLog.retryCount());

		notificationEventLogUseCase.saveEventLog(context.notificationId(), EventStatus.RETRIED, context);

		notificationDispatcher.dispatch(notifierPort, context);
	}

	private RetryPolicy findRetryPolicy(NotificationType notificationType) {
		return retryPolicies.stream()
			.filter(p -> p.support(notificationType))
			.findAny()
			.orElseGet(StandardRetryPolicy::new);
	}
}
