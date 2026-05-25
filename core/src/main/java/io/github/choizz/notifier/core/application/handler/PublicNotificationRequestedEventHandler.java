package io.github.choizz.notifier.core.application.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PublicNotificationRequestedEventHandler {

	private final NotificationLogPersistencePort notificationLogPersistencePort;
	private final NotificationEventPublisher notificationEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void saveEvent(PublicNotificationRequestedEvent event) {

		NotificationLog notificationLog = NotificationLog.requestToPublic(event);

		notificationLogPersistencePort.save(notificationLog);

		log.info("공통 알림 이벤트 저장 완료 - publicNotificationId={}", event.publicNotificationId());
	}

	@Async("taskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void publishNotification(PublicNotificationRequestedEvent event) {

		PublishCommandEvent publishCommandEvent = PublishCommandEvent.builder()
			.notificationId(event.publicNotificationId())
			.subscriberId(event.subscriberId())
			.notificationType(event.notificationType())
			.channel(event.channel())
			.metadata(event.metadata())
			.referenceType(event.referenceType())
			.build();

		notificationEventPublisher.publish(publishCommandEvent);
	}
}
