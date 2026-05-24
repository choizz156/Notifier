package io.github.choizz.notifier.rdb.application.retry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.model.RetryLevel;

@Component
public class AggressiveRetryProcessor extends AbstractRetryProcessor {

	public AggressiveRetryProcessor(
		NotificationEventLogUseCase notificationEventLogUseCase,
		ApplicationEventPublisher applicationEventPublisher
	) {

		super(notificationEventLogUseCase, applicationEventPublisher);
	}

	@Override
	public RetryLevel getRdbRetryLevel() {

		return RetryLevel.AGGRESSIVE;
	}

	@Override
	@AggressiveRetry
	public void handle(NotifierPort notifierPort, PublicationContext context) {

		super.handle(notifierPort, context);
	}
}
