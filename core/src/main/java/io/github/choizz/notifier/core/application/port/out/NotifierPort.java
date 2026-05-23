package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;

public interface NotifierPort {

	boolean supports(String channel);

	void publish(PublishCommandEvent event);
}
