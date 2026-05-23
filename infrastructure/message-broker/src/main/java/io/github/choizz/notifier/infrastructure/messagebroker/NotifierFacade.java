package io.github.choizz.notifier.infrastructure.messagebroker;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotifierFacade {

	private final List<NotifierPort> notifierPorts;

	public NotifierPort getNotifierPort(String channel) {

		return findNotifier(channel);
	}

	private NotifierPort findNotifier(String channel) {

		return notifierPorts.stream()
			.filter(notifierPort -> notifierPort.supports(channel))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("알림 발송 채널을 찾을 수 없습니다."));
	}
}
