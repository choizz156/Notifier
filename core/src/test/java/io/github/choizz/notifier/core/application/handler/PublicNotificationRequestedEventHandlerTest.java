package io.github.choizz.notifier.core.application.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.port.out.DlqPort;
import io.github.choizz.notifier.core.application.support.PublicNotificationBulkProcessor;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;

@ExtendWith(MockitoExtension.class)
class PublicNotificationRequestedEventHandlerTest {

	@Mock
	private PublicNotificationBulkProcessor publicNotificationBulkProcessor;

	@Mock
	private DlqPort dlqPort;

	@InjectMocks
	private PublicNotificationRequestedEventHandler handler;

	@DisplayName("이벤트 처리가 정상적으로 수행되면 프로세서를 호출하고 DLQ는 저장하지 않는다.")
	@Test
	void test1() {
		// given
		PublicNotificationRequestedEvent event = new PublicNotificationRequestedEvent(
			List.of(1L, 2L),
			"{}",
			"NOTICE",
			"key-1"
		);

		// when
		handler.handle(event);

		// then
		verify(publicNotificationBulkProcessor, times(1)).chunkToPublic(event);
		verify(dlqPort, times(0)).saveDlq(any(), any());
	}

	@DisplayName("이벤트 처리 중 런타임 예외가 발생하면 DLQ 포트에 저장을 위임한다.")
	@Test
	void test2() {
		// given
		PublicNotificationRequestedEvent event = new PublicNotificationRequestedEvent(
			List.of(1L, 2L),
			"{}",
			"NOTICE",
			"key-1"
		);
		RuntimeException exception = new RuntimeException("DB Connection Error");

		doThrow(exception).when(publicNotificationBulkProcessor).chunkToPublic(event);

		// when
		handler.handle(event);

		// then
		verify(publicNotificationBulkProcessor, times(1)).chunkToPublic(event);
		verify(dlqPort, times(1)).saveDlq(eq(event), eq(exception));
	}

}
