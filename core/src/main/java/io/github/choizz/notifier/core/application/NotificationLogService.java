package io.github.choizz.notifier.core.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationLogService implements NotificationLogUseCase {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;

	@Override
	public void updateStatus(NotificationEventLog notificationEventLog, EventStatus eventStatus) {

		switch (eventStatus) {
			case FAILED -> notificationEventLog.markAsFailed();
			case RETRIED -> notificationEventLog.markAsRetried();
			case SENT -> notificationEventLog.published();
		}
		notificationEventLogPersistencePort.save(notificationEventLog);
	}
}
