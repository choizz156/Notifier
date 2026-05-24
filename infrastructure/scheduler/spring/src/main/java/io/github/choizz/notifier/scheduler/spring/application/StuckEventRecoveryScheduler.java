package io.github.choizz.notifier.scheduler.spring.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import io.github.choizz.notifier.core.application.port.in.StuckEventRecoveryUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StuckEventRecoveryScheduler {

	private final StuckEventRecoveryUseCase stuckEventRecoveryUseCase;

	@Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
	@SchedulerLock(name = "stuck_event_recovery_lock", lockAtLeastFor = "30s", lockAtMostFor = "5m")
	public void recover() {
		stuckEventRecoveryUseCase.recoverStuckEvents();
	}
}
