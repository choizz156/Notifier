package io.github.choizz.notifier.rdb.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.infrastructure.messagebroker.NotificationDispatcher;
import io.github.choizz.notifier.infrastructure.messagebroker.NotifierFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class RdsNotificationEventPublishAdapter implements NotificationEventPublisher {

	private final NotifierFacade notifierFacade;
	private final NotificationDispatcher notificationDispatcher;

	@Override
	public void publish(PublishCommandEvent event) {
		NotifierPort notifierPort = notifierFacade.getNotifierPort(event.channel());
		PublicationContext context = PublicationContext.of(event);
		notificationDispatcher.dispatch(notifierPort, context);
	}
}
