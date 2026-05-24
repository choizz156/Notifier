package io.github.choizz.notifier.core.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.in.StuckEventRecoveryUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationRetryPolicyPort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StuckEventRecoveryService implements StuckEventRecoveryUseCase {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationPersistencePort notificationPersistencePort;
	private final NotificationRetryPolicyPort notificationRetryPolicyPort;

	@Override
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

	@Transactional
	protected void processStuckLog(NotificationEventLog logEvent, LocalDateTime now) {

		Notification notification = notificationPersistencePort.findById(logEvent.notificationId());
		NotificationType.RetryLevel retryLevel = notification.notificationType().retryLevel();

		long maxProcessingTimeSeconds = notificationRetryPolicyPort.getMaxProcessingTimeSeconds(retryLevel);
		LocalDateTime thresholdTime = now.minusSeconds(maxProcessingTimeSeconds);

		if (logEvent.updatedAt().isBefore(thresholdTime)) {
			recover(logEvent, retryLevel);
			notificationEventLogPersistencePort.save(logEvent);
		}
	}

	private void recover(NotificationEventLog logEvent, NotificationType.RetryLevel retryLevel) {

		int newRetryCount = logEvent.retryCount() + 1;
		int maxAttempts = notificationRetryPolicyPort.getMaxAttempts(retryLevel);

		if (newRetryCount >= maxAttempts) {
			logEvent.markAsFailed("Stuck in processing - Max retry exceeded", newRetryCount);
		} else {
			logEvent.markAsRetried("Stuck in processing - Recovered", newRetryCount);
		}
	}
}
