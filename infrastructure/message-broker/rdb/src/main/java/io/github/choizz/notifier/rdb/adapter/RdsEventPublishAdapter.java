package io.github.choizz.notifier.rdb.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.infrastructure.messagebroker.NotifierFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class RdsEventPublishAdapter implements NotificationEventPublisher {

	private final NotifierFacade notifierFacade;
	private final EventPublishFallbackProcessor eventPublishFallbackProcessor;

	@Override
	public void publish(PublishCommandEvent event) {

		NotifierPort notifierPort = notifierFacade.getNotifierPort(event.channel());

		try {
			notifierPort.publish(event);
		} catch (Exception e) {
			log.info("메시지 발행 실패, id = {}, type = {}", event.notificationId(), event.notificationType());
			eventPublishFallbackProcessor.handle(notifierPort, event.notificationId());
		}
	}
}
