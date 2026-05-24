package io.github.choizz.notifier.scheduler.spring.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.StuckEventRecoveryUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StuckEventRecoveryScheduler {

	private final StuckEventRecoveryUseCase stuckEventRecoveryUseCase;

	@Scheduled(fixedDelay = 300000) // 5분마다 실행
	public void recover() {

		stuckEventRecoveryUseCase.recoverStuckEvents();
	}
}
