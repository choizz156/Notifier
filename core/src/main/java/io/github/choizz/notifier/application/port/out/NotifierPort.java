package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;

public interface NotifierPort {

	boolean supports(String channel);

	void publish(NotificationRequestedEvent event);
}
