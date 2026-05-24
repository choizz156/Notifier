package io.github.choizz.notifier.core.application.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationFailedEventHandler {

	private final NotificationUseCase notificationUseCase;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void updateNotification(PublishFailedEvent event) {
		notificationUseCase.fail(event.notificationId(), event.failReason());
	}
}
