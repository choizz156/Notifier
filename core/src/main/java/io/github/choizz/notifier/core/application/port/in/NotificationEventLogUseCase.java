package io.github.choizz.notifier.core.application.port.in;

import io.github.choizz.notifier.core.application.dto.PublicationFailContext;
import io.github.choizz.notifier.core.domain.model.EventStatus;

public interface NotificationEventLogUseCase {

	void recordEventLog(PublicationFailContext context, EventStatus eventStatus);

	void done(Long notificationId, EventStatus eventStatus);
}
