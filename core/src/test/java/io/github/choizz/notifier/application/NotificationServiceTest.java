package io.github.choizz.notifier.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.core.application.NotificationService;
import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private NotificationPersistencePort persistencePort;

	@Mock
	private MockUserPersistencePort mockUserPersistencePort;

	@InjectMocks
	private NotificationService sut;

	@Test
	@DisplayName("새로운 알림 푸시 요청 시 유저가 동의했고 중복이 아니면 저장 후 이벤트를 발행한다.")
	void test1() {
		// given
		NotificationContext context = new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED.name(), Map.of("key", "value")
		);
		given(mockUserPersistencePort.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED)).willReturn(true);
		given(mockUserPersistencePort.findSubscribedChannels(1L)).willReturn(Set.of(Channel.IN_APP));
		given(persistencePort.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP)).willReturn(false);

		Notification savedNotification = Notification.builder().id(10L).subscriberId(context.subscriberId()).notificationType(context.notificationType()).channel(Channel.IN_APP).metadata("{}").build();
		given(persistencePort.save(any(Notification.class))).willReturn(savedNotification);

		// when
		sut.push(context);

		// then
		verify(persistencePort).save(any(Notification.class));
		ArgumentCaptor<NotificationRequestedEvent> captor = ArgumentCaptor.forClass(NotificationRequestedEvent.class);
		verify(eventPublisher).publishEvent(captor.capture());
		
		NotificationRequestedEvent event = captor.getValue();
		assertThat(event.subscriberId()).isEqualTo(1L);
		assertThat(event.channel()).isEqualTo(Channel.IN_APP.name());
	}

	@Test
	@DisplayName("알림 푸시 요청 시 이미 처리 중인 중복 알림이면 해당 채널은 건너뛴다.")
	void test2() {
		// given
		NotificationContext context = new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED.name(), Map.of()
		);
		given(mockUserPersistencePort.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED)).willReturn(true);
		given(mockUserPersistencePort.findSubscribedChannels(1L)).willReturn(Set.of(Channel.IN_APP));
		given(persistencePort.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP)).willReturn(true);

		// when 
		sut.push(context);

		// then
		verify(persistencePort, never()).save(any(Notification.class));
		verify(eventPublisher, never()).publishEvent(any(NotificationRequestedEvent.class));
	}

	@Test
	@DisplayName("알림 읽음 처리 시 알림의 isRead 상태를 true로 변경하고 저장한다.")
	void test3() {
		// when
		sut.markAsRead(10L);

		// then
		verify(persistencePort).markAsRead(10L);
	}
}
