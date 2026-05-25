package io.github.choizz.notifier.app.runner;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UnprocessedNotificationRecoveryRunner {

	private final NotificationUseCase notificationUseCase;

	@Async("taskExecutor")
	@EventListener(ApplicationReadyEvent.class)
	public void recoverUnprocessedNotifications() {

		log.info("미처리 알림 복구 작업을 시작합니다.");
		try {
			notificationUseCase.retry();
		} catch (Exception e) {
			log.error("알림 재처리 중 오류 발생", e);
		}
		log.info("미처리 알림 복구 작업을 종료합니다.");
	}
}
