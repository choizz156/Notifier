package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.event.PublishCommandEvent;

public interface NotificationEventPublisher {

	void publish(PublishCommandEvent event);
}
