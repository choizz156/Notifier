package io.github.choizz.notifier.infrastructure.messagebroker;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;

public interface NotificationDispatcher {
	void dispatch(NotifierPort notifierPort, PublicationContext context);
}
