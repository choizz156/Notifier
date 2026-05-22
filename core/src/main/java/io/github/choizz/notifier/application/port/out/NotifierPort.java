package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;

public interface NotifierPort {

	void publish(NotificationRequestedEvent event);

	boolean supports(String channel);
}
