package io.github.choizz.notifier.core.application;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.dto.ClaimContext;
import io.github.choizz.notifier.core.application.factory.NotificationLogFactory;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationLogService implements NotificationLogUseCase {

	public static final List<EventStatus> TARGET_STATUSES = List.of(
		EventStatus.REQUESTED, EventStatus.RETRIED, EventStatus.FAILED
	);

	private final NotificationLogPersistencePort notificationLogPersistencePort;
	private final NotificationLogFactory notificationLogFactory;

	@Override
	public void saveAll(List<NotificationLog> notificationLogs) {

		notificationLogPersistencePort.saveAll(notificationLogs);
	}

	@Override
	public void saveNotificationLog(Long notificationId, EventStatus eventStatus, PublicationContext context) {

		NotificationLog notificationLog = notificationLogFactory.create(eventStatus, context);
		notificationLogPersistencePort.save(notificationLog);
	}

	@Override
	public boolean tryClaim(ClaimContext context) {

		try {
			Optional<NotificationLog> optionalLog =
				notificationLogPersistencePort.findByUniqueKey(context);

			if (optionalLog.isEmpty()) {
				return false;
			}

			NotificationLog notificationLog = optionalLog.get();

			if (notificationLog.eventStatus() != EventStatus.REQUESTED
				&& notificationLog.eventStatus() != EventStatus.RETRIED
				&& notificationLog.eventStatus() != EventStatus.FAILED) {
				return false;
			}

			notificationLog.markAsProcessing();
			notificationLogPersistencePort.save(notificationLog);
			return true;
		} catch (OptimisticLockingFailureException e) {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findUnprocessedNotificationIds(Long lastId, int chunkSize) {
		List<Long> ids = notificationLogPersistencePort.findUnprocessedNotificationIds(
			TARGET_STATUSES, lastId, chunkSize
		);
		log.info("findUnprocessedNotificationIds(lastId={}, chunkSize={}) 반환 결과: {}", lastId, chunkSize, ids);
		return ids;
	}

	@Override
	public void save(NotificationLog notificationLog) {
		notificationLogPersistencePort.save(notificationLog);
	}
}
