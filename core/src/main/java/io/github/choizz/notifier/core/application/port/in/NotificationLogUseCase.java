package io.github.choizz.notifier.core.application.port.in;

import java.util.List;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.application.dto.ClaimContext;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

public interface NotificationLogUseCase {

	void saveAll(List<NotificationLog> notificationLogs);

	void saveNotificationLog(Long notificationId, EventStatus eventStatus, PublicationContext context);

	boolean tryClaim(ClaimContext context);

	List<Long> findUnprocessedNotificationIds(Long lastId, int chuckSize);

	void save(NotificationLog notificationLog);
}
