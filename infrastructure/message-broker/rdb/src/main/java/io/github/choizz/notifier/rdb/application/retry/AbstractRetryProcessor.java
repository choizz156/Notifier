package io.github.choizz.notifier.rdb.application.retry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.support.RetrySynchronizationManager;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.RetryLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRetryProcessor implements RetryProcessor {

	private final NotificationLogUseCase notificationLogUseCase;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public boolean support(NotificationType type) {

		return RetryLevel.from(type) == getRdbRetryLevel();
	}

	@Override
	public void handle(NotifierPort notifierPort, PublicationContext context) {

		int currentRetryCount = RetrySynchronizationManager.getContext().getRetryCount();

		context.updateRetryCount(currentRetryCount);
		notificationLogUseCase.saveNotificationLog(context.notificationId(), EventStatus.RETRIED, context);

		send(notifierPort, context);
	}

	@Recover
	public void fail(Exception e, NotifierPort notifierPort, PublicationContext context) {
		applicationEventPublisher.publishEvent(new PublishFailedEvent(context));
	}

	@Override
	public abstract RetryLevel getRdbRetryLevel();

	private void send(NotifierPort notifierPort, PublicationContext context) {

		notifierPort.publish(context);
		applicationEventPublisher.publishEvent(new PublishCompletedEvent(context));
	}
}
