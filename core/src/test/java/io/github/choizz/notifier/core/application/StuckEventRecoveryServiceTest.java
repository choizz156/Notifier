package io.github.choizz.notifier.core.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.config.RetryProperties;
import io.github.choizz.notifier.core.application.config.RetryProperties.RetryConfig;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;

@ExtendWith(MockitoExtension.class)
class StuckEventRecoveryServiceTest {

	@Mock
	private NotificationLogPersistencePort notificationLogPersistencePort;

	@Mock
	private NotificationUseCase notificationUseCase;

	@Mock
	private RetryProperties retryProperties;

	@InjectMocks
	private StuckEventRecoveryService stuckEventRecoveryService;

	@DisplayName("PROCESSING 상태로 오랫동안 멈춰있는(Stuck) 이벤트를 조회하여 복구 처리를 한다.")
	@Test
	void test1() {
		// given
		RetryConfig config = new RetryConfig();
		config.setMaxAttempts(3);
		config.setMaxProcessingTimeSeconds(300);
		config.setDelay(100);
		config.setMaxDelay(2);
		when(retryProperties.getConfig(any())).thenReturn(config);

		NotificationLog stuckLog1 = NotificationLog.builder().id(1L).notificationId(10L).eventStatus(EventStatus.PROCESSING).build();
		NotificationLog stuckLog2 = NotificationLog.builder().id(2L).notificationId(20L).eventStatus(EventStatus.PROCESSING).build();

		// 첫 페이지 조회
		when(notificationLogPersistencePort.findStuckLogs(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.eq(EventStatus.PROCESSING), org.mockito.ArgumentMatchers.anyCollection(), org.mockito.ArgumentMatchers.any(java.time.LocalDateTime.class), org.mockito.ArgumentMatchers.anyInt()))
			.thenReturn(List.of(stuckLog1, stuckLog2))
			.thenReturn(List.of()); // 이후 조회시 빈 리스트로 루프 종료

		// when
		stuckEventRecoveryService.recoverStuckEvents();

		// then
		// 한 번의 청크 조회가 성공하여 처리되므로 각각 1번 호출된다.
		verify(notificationLogPersistencePort, times(1)).saveAll(anyList());
		verify(notificationUseCase, times(1)).retryStuckNotification(anyList());
	}
}
