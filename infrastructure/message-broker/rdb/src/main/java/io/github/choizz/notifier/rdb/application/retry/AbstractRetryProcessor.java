package io.github.choizz.notifier.rdb.application.retry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.support.RetrySynchronizationManager;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRetryProcessor implements RetryProcessor {

	private final NotificationEventLogUseCase notificationEventLogUseCase;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public boolean support(NotificationType type) {

		return type.retryLevel() == getRetryLevel();
	}

	@Override
	public void handle(NotifierPort notifierPort, PublicationContext context) {

		int currentRetryCount = RetrySynchronizationManager.getContext().getRetryCount();

		context.updateRetryCount(currentRetryCount);
		notificationEventLogUseCase.saveEventLog(context.notificationId(), EventStatus.RETRIED, context);

		send(notifierPort, context);
	}

	@Recover
	protected void fail(Exception e, NotifierPort notifierPort, PublicationContext context) {

		notificationEventLogUseCase.saveEventLog(
			context.notificationId(),
			EventStatus.FAILED,
			context.notSent(e.getMessage())
		);
	}

	@Override
	public abstract NotificationType.RetryLevel getRetryLevel();

	private void send(NotifierPort notifierPort, PublicationContext context) {

		notifierPort.publish(context);
		applicationEventPublisher.publishEvent(new PublishCompletedEvent(context));
	}
}
