package io.github.choizz.notifier.core.application.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequestedEventHandler {

	private final NotificationLogPersistencePort notificationLogPersistencePort;
	private final NotificationEventPublisher notificationEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void saveEvent(NotificationRequestedEvent event) {

		NotificationLog notificationLog = NotificationLog.request(
			event.notificationId(),
			ReferenceType.PERSONAL,
			NotificationType.valueOf(event.notificationType()),
			Channel.valueOf(event.channel()),
			JsonUtils.toJson(event.metadata())
		);

		notificationLogPersistencePort.save(notificationLog);

		log.info("알림 이벤트 저장 완료 - notificationId={}", event.notificationId());
	}

	@Async("taskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void publishNotification(NotificationRequestedEvent event) {

		PublishCommandEvent publishCommandEvent = PublishCommandEvent.of(event, event.metadata());
		notificationEventPublisher.publish(publishCommandEvent);
	}
}
