package io.github.choizz.notifier.core.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.choizz.notifier.core.application.config.RetryProperties;
import io.github.choizz.notifier.core.application.config.RetryProperties.RetryConfig;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.in.StuckEventRecoveryUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.RetryLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StuckEventRecoveryService implements StuckEventRecoveryUseCase {

	private final NotificationLogPersistencePort notificationLogPersistencePort;
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
				NotificationLog::id,
				lastId -> notificationLogPersistencePort.findStuckLogs(
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

	private void recover(List<NotificationLog> stuckLogs) {
		for (NotificationLog log : stuckLogs) {
			log.markAsFailed("Stuck Timeout Recovery", log.retryCount());
		}
		notificationLogPersistencePort.saveAll(stuckLogs);

		List<Long> notificationIds = stuckLogs.stream()
			.map(NotificationLog::notificationId)
			.toList();

		notificationUseCase.retryStuckNotification(notificationIds);
	}
}
