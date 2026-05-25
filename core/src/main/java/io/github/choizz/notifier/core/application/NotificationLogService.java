package io.github.choizz.notifier.core.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.factory.NotificationLogFactory;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationLogService implements NotificationLogUseCase {

	public static final List<EventStatus> targetStatuses = List.of(
		EventStatus.REQUESTED, EventStatus.RETRIED, EventStatus.PROCESSING
	);

	private final ApplicationEventPublisher applicationEventPublisher;
	private final NotificationLogPersistencePort notificationLogPersistencePort;
	private final NotificationLogFactory notificationLogFactory;

	@Override
	public void savenotificationLog(Long notificationId, EventStatus eventStatus, PublicationContext context) {

		NotificationLog notificationLog = notificationLogFactory.create(eventStatus, context);
		notificationLogPersistencePort.save(notificationLog);
	}

	@Override
	public boolean tryClaim(Long notificationId) {

		NotificationLog notificationLog =
			notificationLogPersistencePort.findLatestByReference(notificationId, ReferenceType.PERSONAL);

		if (notificationLog.eventStatus() != EventStatus.REQUESTED && notificationLog.eventStatus() != EventStatus.RETRIED) {
			return false;
		}

		notificationLog.markAsProcessing();

		try {
			notificationLogPersistencePort.save(notificationLog);
			return true;
		} catch (OptimisticLockingFailureException e) {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findUnprocessedNotificationIds(Long lastId, int chunkSize) {

		return notificationLogPersistencePort.findUnprocessedNotificationIds(
			targetStatuses, lastId, chunkSize
		);
	}
}
