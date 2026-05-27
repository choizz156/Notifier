package io.github.choizz.notifier.app.runner;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

@Slf4j
@RequiredArgsConstructor
@Component
public class UnprocessedNotificationRecoveryRunner {

	private final NotificationUseCase notificationUseCase;
	private final LockProvider lockProvider;

	@Async("taskExecutor")
	@EventListener(ApplicationReadyEvent.class)
	public void recoverUnprocessedNotifications() {

		LockConfiguration lockConfiguration = new LockConfiguration(
			Instant.now(),
			"recoverUnprocessedNotifications",
			Duration.ofMinutes(5),
			Duration.ofSeconds(30)
		);

		Optional<SimpleLock> lock = lockProvider.lock(lockConfiguration);

		if (lock.isEmpty()) {
			log.info("미처리 알림 복구 작업의 락을 획득하지 못해 스킵합니다. (다른 인스턴스에서 수행 중)");
			return;
		}

		try {
			log.info("미처리 알림 복구 작업을 시작합니다.");
			notificationUseCase.retry();
		} catch (Exception e) {
			log.error("알림 재처리 중 오류 발생", e);
		} finally {
			log.info("미처리 알림 복구 작업을 종료합니다.");
			lock.get().unlock();
		}
	}
}
