package io.github.choizz.notifier.core.application.handler;

import org.jspecify.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventType;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequestedEventHandler {
	private final NotificationEventLogPersistencePort eventLogPersistencePort;
	private final NotificationEventPublisher notificationEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void saveEvent(NotificationRequestedEvent event) {

		NotificationEventLog eventLog = NotificationEventLog.request(
			event.notificationId(),
			Channel.valueOf(event.channel()),
			getMetadataToString(event)
		);

		eventLogPersistencePort.save(eventLog);

		log.info("알림 이벤트 저장 완료 - notificationId={}", event.notificationId());
	}

	@Async("taskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void publishNotification(NotificationRequestedEvent event) {

		PublishCommandEvent publishCommandEvent = PublishCommandEvent.of(event, event.metadata());
		notificationEventPublisher.publish(publishCommandEvent);
	}

	private @Nullable String getMetadataToString(NotificationRequestedEvent event){
		return JsonUtils.toJson(event.metadata());
	}

	// private void handleFailure(NotificationRequestedEvent event, String error) {
	// 	NotificationEventLog latestLog = eventLogPersistencePort.findLatestByNotificationId(event.notificationId());
	// 	int nextRetryCount = (latestLog != null) ? latestLog.retryCount() + 1 : 1;
	//
	// 	NotificationEventLog failLog = NotificationEventLog.fail(
	// 		event.notificationId(),
	// 		EventType.FAILED,
	// 		error,
	// 		nextRetryCount
	// 	);
	// 	eventLogPersistencePort.save(failLog);
	//
	// 	if (retryPolicy.canRetry(failLog)) {
	// 		log.info("알림 재시도 시도 - notificationId={}, retryCount={}", event.notificationId(), nextRetryCount);
	// 		eventPublisher.publishEvent(event);
	// 	} else {
	// 		log.warn("알림 재시도 횟수 초과 - notificationId={}", event.notificationId());
	// 	}
	// }
}
