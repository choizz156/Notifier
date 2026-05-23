package io.github.choizz.notifier.rdb.application;

import java.util.List;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.infrastructure.messagebroker.NotificationDispatcher;
import io.github.choizz.notifier.infrastructure.messagebroker.retry.RetryPolicy;
import io.github.choizz.notifier.infrastructure.messagebroker.retry.StandardRetryPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PublishRetryListener implements RetryListener {
	private final List<RetryPolicy> retryPolicies;
	private final NotificationDispatcher notificationDispatcher;
	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationEventLogUseCase notificationEventLogUseCase;

	@Override
	public <T, E extends Throwable> void onError(
		RetryContext context, RetryCallback<T, E> callback,
		Throwable throwable
	) {


		RetryListener.super.onError(context, callback, throwable);
	}

	private RetryPolicy findRetryPolicy(NotificationType notificationType) {
		return retryPolicies.stream()
			.filter(p -> p.support(notificationType))
			.findAny()
			.orElseGet(StandardRetryPolicy::new);
	}

}
