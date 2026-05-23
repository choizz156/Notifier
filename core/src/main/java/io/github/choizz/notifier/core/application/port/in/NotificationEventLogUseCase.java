package io.github.choizz.notifier.core.application.port.in;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.EventStatus;

public interface NotificationEventLogUseCase {
	void saveEventLog(Long notificationId, EventStatus eventStatus, PublicationContext context);
}
