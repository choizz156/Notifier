package io.github.choizz.notifier.rdb.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.infrastructure.messagebroker.NotificationDispatcher;
import io.github.choizz.notifier.rdb.application.retry.RetryProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RdsNotificationDispatcher implements NotificationDispatcher {

	private final List<RetryProcessor> retryProcessors;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void dispatch(NotifierPort notifierPort, PublicationContext context) {

		RetryProcessor retryProcessor = findRetryProcessor(NotificationType.valueOf(context.notificationType()));

		try {
			notifierPort.publish(context);
			applicationEventPublisher.publishEvent(new PublishCompletedEvent(context));
		} catch (Exception e) {
			log.info("메시지 발행 실패, id = {}, type = {}", context.notificationId(), context.notificationType());
			retryProcessor.handle(notifierPort, context.notSent(e.getMessage()));
		}
	}

	private RetryProcessor findRetryProcessor(NotificationType notificationType) {

		return retryProcessors.stream()
			.filter(processor -> processor.support(notificationType))
			.findFirst()
			.orElse(retryProcessors.getFirst());
	}
}
