package io.github.choizz.notifier.rdb.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.infrastructure.messagebroker.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RdsNotificationDispatcher implements NotificationDispatcher {

	private final PublishFallbackProcessor publishFallbackProcessor;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void dispatch(NotifierPort notifierPort, PublicationContext context) {

		try {
			notifierPort.publish(context);
			applicationEventPublisher.publishEvent(new PublishCompletedEvent(context));
		} catch (Exception e) {
			log.info("메시지 발행 실패, id = {}, type = {}", context.notificationId(), context.notificationType());
			publishFallbackProcessor.handle(notifierPort, context.notSent(e.getMessage()));
		}
	}
}
