package io.github.choizz.notifier.rdb.application.retry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.model.RetryLevel;

@Component
public class StandardRetryProcessor extends AbstractRetryProcessor {

	public StandardRetryProcessor(
		NotificationLogUseCase notificationLogUseCase,
		ApplicationEventPublisher applicationEventPublisher
	) {

		super(notificationLogUseCase, applicationEventPublisher);
	}

	@Override
	public RetryLevel getRdbRetryLevel() {

		return RetryLevel.STANDARD;
	}

	@Override
	@StandardRetry
	public void handle(NotifierPort notifierPort, PublicationContext context) {

		super.handle(notifierPort, context);
	}
}
