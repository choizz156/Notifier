package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.application.dto.PublicationContext;

public interface NotifierPort {

	boolean supports(String channel);

	void publish(PublicationContext context);
}
