package io.github.choizz.notifier.core.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.choizz.notifier.core.application.config.RetryProperties;
import io.github.choizz.notifier.core.application.config.RetryProperties.RetryConfig;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.in.StuckEventRecoveryUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.model.RetryLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StuckEventRecoveryService implements StuckEventRecoveryUseCase {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationUseCase notificationUseCase;
	private final RetryProperties retryProperties;

	@Override
	public void recoverStuckEvents() {

		LocalDateTime now = LocalDateTime.now();

		for (RetryLevel level : RetryLevel.values()) {
			if (level == RetryLevel.NONE || level.supportedTypes().isEmpty()) {
				continue;
			}

			RetryConfig config = retryProperties.getConfig(level);
			LocalDateTime thresholdTime = now.minusSeconds(config.getMaxProcessingTimeSeconds());

			ChunkExecutor.execute(
				0L,
				NotificationEventLog::id,
				lastId -> notificationEventLogPersistencePort.findStuckLogs(
					lastId,
					EventStatus.PROCESSING,
					level.supportedTypes(),
					thresholdTime,
					ChunkExecutor.CHUNK_SIZE
				),
				this::recover
			);
		}
	}

	private void recover(List<NotificationEventLog> stuckLogs) {
		for (NotificationEventLog log : stuckLogs) {
			log.markAsFailed("Stuck Timeout Recovery", log.retryCount());
		}
		notificationEventLogPersistencePort.saveAll(stuckLogs);

		List<Long> notificationIds = stuckLogs.stream()
			.map(NotificationEventLog::notificationId)
			.toList();

		notificationUseCase.retryStuckNotification(notificationIds);
	}
}
