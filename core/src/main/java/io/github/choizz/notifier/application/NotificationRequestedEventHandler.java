package io.github.choizz.notifier.application;

import org.jspecify.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.choizz.notifier.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.domain.event.PushCommandEvent;
import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.notifier.domain.model.NotificationEventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequestedEventHandler {

	private final ObjectMapper objectMapper;
	private final NotificationEventLogPersistencePort eventLogPersistencePort;
	private final NotificationEventPublisher notificationEventPublisher;
	private final NotifierFacade notifierFacade;

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

		PushCommandEvent pushCommandEvent = PushCommandEvent.of(event, getMetadataToString(event));
		notificationEventPublisher.publish(pushCommandEvent);
		// try {
		// 	notifierFacade.publish(event);
		// } catch (Exception e) {
		// 	log.error("알림 발송 실패 - notificationId={}, error={}", event.notificationId(), e.getMessage());
		// 	handleFailure(event, e.getMessage());
		// }
	}

	private @Nullable String getMetadataToString(NotificationRequestedEvent event){
		try {
			return event.metadata() != null ? objectMapper.writeValueAsString(event.metadata()) : null;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
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
