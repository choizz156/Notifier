package io.github.choizz.notifier.scheduler.spring.application;

import static org.mockito.Mockito.never;
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
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.core.application.dto.DlqRecoveryTarget;
import io.github.choizz.notifier.core.application.port.out.DlqPort;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;

@ExtendWith(MockitoExtension.class)
class PublicNotificationDlqRecoverySchedulerTest {

	@Mock
	private DlqPort dlqPort;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private PublicNotificationDlqRecoveryScheduler scheduler;

	@DisplayName("조회된 DLQ 항목이 없으면 바로 종료한다.")
	@Test
	void test1() {
		// given
		when(dlqPort.findPendingDlqs(100)).thenReturn(List.of());

		// when
		scheduler.recoverDlq();

		// then
		verify(eventPublisher, never()).publishEvent(null);
	}

	@DisplayName("조회된 DLQ 항목을 RESOLVED로 변경하고 이벤트를 재발행한다.")
	@Test
	void test2() {
		// given
		PublicNotificationRequestedEvent event = new PublicNotificationRequestedEvent(List.of(1L), "{}", "NOTICE", "key");
		DlqRecoveryTarget target = DlqRecoveryTarget.builder().dlqId(1L).event(event).build();
		when(dlqPort.findPendingDlqs(100)).thenReturn(List.of(target));

		// when
		scheduler.recoverDlq();

		// then
		verify(dlqPort, times(1)).markAsResolved(1L);
		verify(eventPublisher, times(1)).publishEvent(event);
	}
}
