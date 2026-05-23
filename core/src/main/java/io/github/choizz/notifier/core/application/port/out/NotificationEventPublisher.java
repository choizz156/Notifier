package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;

public interface NotificationEventPublisher {

	void publish(PublishCommandEvent event);
}
