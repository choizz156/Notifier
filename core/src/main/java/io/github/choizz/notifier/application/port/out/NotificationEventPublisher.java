package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.event.PushCommandEvent;

public interface NotificationEventPublisher {

	void publish(PushCommandEvent event);
}
