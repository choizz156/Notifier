package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.event.PublishCommandEvent;

public interface NotifierPort {

	boolean supports(String channel);

	void publish(PublishCommandEvent event);
}
