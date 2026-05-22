package io.github.choizz.notifier.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.application.dto.NotificationContext;
import io.github.choizz.notifier.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.notifier.domain.model.Notification;
import io.github.choizz.notifier.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationApplicationServiceTest {

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private NotificationPersistencePort persistencePort;

	@InjectMocks
	private NotificationApplicationService sut;

	@Test
	@DisplayName("새로운 알림 푸시 요청 시 중복이 아니면 저장 후 이벤트를 발행한다.")
	void test1() {
		// given
		NotificationContext context = new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of("key", "value")
		);
		given(persistencePort.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP)).willReturn(false);

		Notification savedNotification = Notification.builder().id(10L).subscriberId(context.subscriberId()).notificationType(context.notificationType()).channel(context.channel()).metadata("{}").build();
		given(persistencePort.save(any(Notification.class))).willReturn(savedNotification);

		// when
		sut.push(context);

		// then
		verify(persistencePort).save(any(Notification.class));
		ArgumentCaptor<NotificationRequestedEvent> captor = ArgumentCaptor.forClass(NotificationRequestedEvent.class);
		verify(eventPublisher).publishEvent(captor.capture());
		
		NotificationRequestedEvent event = captor.getValue();
		assertThat(event.subscriberId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("알림 푸시 요청 시 이미 처리 중인 중복 알림이면 예외가 발생한다.")
	void test2() {
		// given
		NotificationContext context = new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of()
		);
		given(persistencePort.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> sut.push(context))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("이미 처리 중인 동일한 알람이 존재합니다.");
	}

	@Test
	@DisplayName("알림 읽음 처리 시 알림의 isRead 상태를 true로 변경하고 저장한다.")
	void test3() {
		// given
		NotificationContext context = new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of()
		);
		Notification notification = Notification.from(context);
		given(persistencePort.findById(10L)).willReturn(notification);

		// when
		sut.markAsRead(10L);

		// then
		assertThat(notification.isRead()).isTrue();
		verify(persistencePort).save(notification);
	}
}
