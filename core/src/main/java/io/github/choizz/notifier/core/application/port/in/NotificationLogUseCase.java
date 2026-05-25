package io.github.choizz.notifier.core.application.port.in;

import java.util.List;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.EventStatus;

public interface NotificationLogUseCase {

	void savenotificationLog(Long notificationId, EventStatus eventStatus, PublicationContext context);

	boolean tryClaim(Long notificationId);

	List<Long> findUnprocessedNotificationIds(Long lastId, int chuckSize);
}
