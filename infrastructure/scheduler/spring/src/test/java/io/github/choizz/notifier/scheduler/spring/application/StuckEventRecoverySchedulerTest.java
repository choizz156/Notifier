package io.github.choizz.notifier.scheduler.spring.application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.port.in.StuckEventRecoveryUseCase;

@ExtendWith(MockitoExtension.class)
class StuckEventRecoverySchedulerTest {

	@Mock
	private StuckEventRecoveryUseCase stuckEventRecoveryUseCase;

	@InjectMocks
	private StuckEventRecoveryScheduler scheduler;

	@DisplayName("멈춰있는 이벤트를 복구하는 스케줄러를 실행한다.")
	@Test
	void test1() {
		// when
		scheduler.recover();

		// then
		verify(stuckEventRecoveryUseCase, times(1)).recoverStuckEvents();
	}
}
