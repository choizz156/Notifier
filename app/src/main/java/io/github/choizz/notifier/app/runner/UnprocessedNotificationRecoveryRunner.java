package io.github.choizz.notifier.app.runner;

import java.util.List;
import java.util.concurrent.Executors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UnprocessedNotificationRecoveryRunner {

	private final NotificationEventLogPersistencePort notificationEventLogPersistencePort;
	private final NotificationUseCase notificationUseCase;

	@EventListener(ApplicationReadyEvent.class)
	public void recoverUnprocessedNotifications() {
		log.info("미처리 알림 복구 작업을 시작합니다.");

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			List<EventStatus> targetStatuses = List.of(EventStatus.REQUESTED, EventStatus.RETRIED, EventStatus.PROCESSING);
			List<Long> notificationIds = notificationEventLogPersistencePort.findUnprocessedNotificationIds(targetStatuses);

			log.info("총 {} 개의 미처리 알림이 발견되었습니다.", notificationIds.size());

			for (Long id : notificationIds) {
				executor.submit(() -> {
					try {
						log.info("가상스레드를 사용하여 미처리 알림(ID: {}) 재처리를 시도합니다.", id);
						notificationUseCase.retry(id);
					} catch (Exception e) {
						log.error("알림(ID: {}) 재처리 중 오류 발생", id, e);
					}
				});
			}
		} catch (Exception e) {
			log.error("미처리 알림 복구 작업 중 치명적 오류 발생", e);
		}
	}
}
