package io.github.choizz.notifier.application;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.application.port.out.NotificationEventPublisher;
import io.github.choizz.notifier.application.port.out.NotifierPort;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotifierFacade {

	private final List<NotifierPort> notifierPorts;

	public void publish(NotificationRequestedEvent event){
		NotifierPort notifier = findNotifier(event.channel());
		notifier.publish(event);
	}

	private NotifierPort findNotifier(String channel) {
		return notifierPorts.stream()
			.filter(notifierPort -> notifierPort.supports(channel))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("알림 발송 채널을 찾을 수 없습니다."));
	}
}
