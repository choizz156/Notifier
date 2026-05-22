package io.github.choizz.notifier.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.github.choizz.notifier.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.domain.model.EventType;
import io.github.choizz.notifier.domain.model.NotificationEventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequestedEventHandler {

	private final NotificationEventLogPersistencePort eventLogPersistencePort;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void saveEvent(NotificationRequestedEvent event) {

		log.info("알림 이벤트 저장 시작 - notificationId={}, subscriberId={}, type={}, channel={}",
			event.notificationId(), event.subscriberId(), event.notificationType(), event.channel());

		NotificationEventLog eventLog = NotificationEventLog.request(
			event.notificationId(),
			EventType.REQUESTED
		);

		eventLogPersistencePort.save(eventLog);

		log.info("알림 이벤트 저장 완료 - notificationId={}", event.notificationId());
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void publishNotification(NotificationRequestedEvent event) {
		// 계층 있어야돼 이걸 구별할
	}
}

