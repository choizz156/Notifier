package io.github.choizz.notifier.core.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.PublicationFailContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationEventLogService implements NotificationEventLogUseCase {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;

	@Override
	public void recordEventLog(PublicationFailContext context, EventStatus eventStatus) {

		NotificationEventLog retriedEventLog = NotificationEventLog.retried(
			context.publishCommandEvent().notificationId(),
			Channel.valueOf(context.publishCommandEvent().channel()),
			context.failReason(),
			JsonUtils.toJson(context.publishCommandEvent().metadata()),
			context.retryCount()
		);

		notificationEventLogPersistencePort.save(retriedEventLog);
	}

	@Override
	public void done(Long notificationId, EventStatus eventStatus) {

		NotificationEventLog eventLog = notificationEventLogPersistencePort.findLatestByNotificationId(notificationId);

		NotificationEventLog sentEventLog = NotificationEventLog.sent(
			eventLog.notificationId(),
			eventLog.channelType(),
			eventLog.metadata(),
			eventLog.retryCount()
		);

		notificationEventLogPersistencePort.save(sentEventLog);
	}
}
