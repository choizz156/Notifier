package io.github.choizz.notifier.rdb.adapter;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
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
	private final NotificationEventLogUseCase notificationEventLogUseCase;

	@Override
	public void publish(PublishCommandEvent event) {

		boolean isClaim = notificationEventLogUseCase.tryClaim(event.notificationId());
		if(!isClaim){
			throw new OptimisticLockingFailureException("이미 처리 중인 알람입니다.");
		}

		NotifierPort notifierPort = notifierFacade.getNotifierPort(event.channel());
		PublicationContext context = PublicationContext.of(event);
		notificationDispatcher.dispatch(notifierPort, context);
	}
}
