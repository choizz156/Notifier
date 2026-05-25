package io.github.choizz.notifier.core.application.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationCompletedEventHandlerTest {

	@Mock
	private NotificationLogUseCase notificationLogUseCase;

	@Mock
	private NotificationUseCase notificationUseCase;

	@InjectMocks
	private NotificationCompletedEventHandler handler;

	@DisplayName("알림 발송 완료 이벤트가 발생하면 상태를 COMPLETED로 변경하고 SENT 로그를 저장한다.")
	@Test
	void test1() {
		// given
		PublishCompletedEvent event = new PublishCompletedEvent(
			1L,
			NotificationType.PAYMENT_CONFIRMED.name(),
			Channel.EMAIL.name(),
			"{}"
		);

		// when
		handler.handleNotificationCompleted(event);

		// then
		verify(notificationUseCase, times(1)).updateStatus(1L, NotificationStatus.COMPLETED);
		verify(notificationLogUseCase, times(1)).savenotificationLog(eq(1L), eq(EventStatus.SENT), any(PublicationContext.class));
	}

	@DisplayName("알림 상태 업데이트나 로그 저장 중 예외가 발생하면 RuntimeException으로 던진다.")
	@Test
	void test2() {
		// given
		PublishCompletedEvent event = new PublishCompletedEvent(
			1L,
			NotificationType.PAYMENT_CONFIRMED.name(),
			Channel.EMAIL.name(),
			"{}"
		);
		doThrow(new IllegalStateException("DB error")).when(notificationUseCase).updateStatus(1L, NotificationStatus.COMPLETED);

		// when & then
		assertThatThrownBy(() -> handler.handleNotificationCompleted(event))
			.isInstanceOf(RuntimeException.class)
			.hasCauseInstanceOf(IllegalStateException.class);
	}
}
