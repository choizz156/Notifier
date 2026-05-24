package io.github.choizz.notifier.rdb.application.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.rdb.application.retry.RdbRetryLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StuckEventRecoveryJob {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationPersistencePort notificationPersistencePort;

	@Scheduled(fixedDelay = 300000) // 5분마다 실행
	public void recoverStuckEvents() {
		List<NotificationEventLog> processingLogs = notificationEventLogPersistencePort.findAllByEventStatus(EventStatus.PROCESSING);

		LocalDateTime now = LocalDateTime.now();

		for (NotificationEventLog logEvent : processingLogs) {
			try {
				processStuckLog(logEvent, now);
			} catch (OptimisticLockingFailureException e) {
				log.info("다른 인스턴스에서 이미 복구를 처리했습니다. logId={}", logEvent.id());
			} catch (Exception e) {
				log.error("고착 상태 로그 복구 중 오류 발생. logId={}", logEvent.id(), e);
			}
		}
	}

	private void processStuckLog(NotificationEventLog logEvent, LocalDateTime now) {
		Notification notification = notificationPersistencePort.findById(logEvent.notificationId());
		NotificationType type = notification.notificationType();

		RdbRetryLevel retryLevel = RdbRetryLevel.from(type);
		LocalDateTime thresholdTime = now.minusSeconds(retryLevel.getMaxProcessingTimeSeconds());

		if (logEvent.updatedAt().isBefore(thresholdTime)) {
			recover(logEvent, retryLevel.getMaxAttempts());
			notificationEventLogPersistencePort.save(logEvent); // save 시점에 Optimistic Lock 검사 수행
		}
	}

	private void recover(NotificationEventLog logEvent, int maxAttempts) {
		int newRetryCount = logEvent.retryCount() + 1;

		if (newRetryCount >= maxAttempts) {
			logEvent.markAsFailed("Stuck in processing - Max retry exceeded", newRetryCount);
		} else {
			logEvent.markAsRetried("Stuck in processing - Recovered", newRetryCount);
		}
	}
}
