package io.github.choizz.notifier.core.application.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PublicNotificationPublishEventHandler {

	private final NotificationEventPublisher notificationEventPublisher;

	@Async("taskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(PublishCommandEvent event) {
		notificationEventPublisher.publish(event);
	}
}
