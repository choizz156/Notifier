package io.github.choizz.notifier.rdb.application.retry;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface RetryProcessor {

	boolean support(NotificationType type);

	void handle(NotifierPort notifierPort, PublicationContext context);
}
