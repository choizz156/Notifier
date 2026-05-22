package io.github.choizz.notifier.infrastructure.messagebroker;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.application.port.out.NotifierPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventPublishAdapter implements NotificationEventPublisher {

	private final List<NotifierPort> notifierPorts;
	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;

	@Override
	public void publish(PublishCommandEvent event) {
		NotifierPort notifierPort = findNotifier(event.channel());
		notifierPort.publish(event);
	}

	private NotifierPort findNotifier(String channel) {
		return notifierPorts.stream()
			.filter(notifierPort -> notifierPort.supports(channel))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("알림 발송 채널을 찾을 수 없습니다."));
	}
}
