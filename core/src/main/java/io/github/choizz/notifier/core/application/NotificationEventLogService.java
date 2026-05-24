package io.github.choizz.notifier.core.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.factory.NotificationEventLogFactory;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.event.PublishFailedEvent;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationEventLogService implements NotificationEventLogUseCase {

	public static final List<EventStatus> targetStatuses = List.of(
		EventStatus.REQUESTED, EventStatus.RETRIED, EventStatus.PROCESSING
	);

	private final ApplicationEventPublisher applicationEventPublisher;
	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationEventLogFactory notificationEventLogFactory;

	@Override
	public void saveEventLog(Long notificationId, EventStatus eventStatus, PublicationContext context) {

		NotificationEventLog eventLog = notificationEventLogFactory.create(eventStatus, context);
		notificationEventLogPersistencePort.save(eventLog);

		if (eventStatus == EventStatus.FAILED) {
			applicationEventPublisher.publishEvent(new PublishFailedEvent(notificationId, context.failReason()));
		}
	}

	@Override
	public boolean tryClaim(Long notificationId) {

		NotificationEventLog eventLog =
			notificationEventLogPersistencePort.findLatestByNotificationId(notificationId);

		if (eventLog.eventStatus() != EventStatus.REQUESTED && eventLog.eventStatus() != EventStatus.RETRIED) {
			return false;
		}

		eventLog.markAdProcessing();

		try {
			notificationEventLogPersistencePort.save(eventLog);
			return true;
		} catch (OptimisticLockingFailureException e) {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findUnprocessedNotificationIds(Long lastId, int chunkSize) {

		return notificationEventLogPersistencePort.findUnprocessedNotificationIds(
			targetStatuses, lastId, chunkSize
		);
	}
}
