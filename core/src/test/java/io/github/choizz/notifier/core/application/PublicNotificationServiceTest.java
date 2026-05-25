package io.github.choizz.notifier.core.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;

@ExtendWith(MockitoExtension.class)
class PublicNotificationServiceTest {

	@Mock
	private PublicNotificationPersistencePort publicNotificationPersistencePort;

	@Mock
	private MockUserPersistencePort mockUserPersistencePort;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private PublicNotificationService publicNotificationService;

	@DisplayName("공통 알림을 읽음 처리한다.")
	@Test
	void test1() {
		// given
		Long subscriberId = 1L;
		Long publicNotificationId = 100L;
		when(publicNotificationPersistencePort.existsReceipt(subscriberId, publicNotificationId)).thenReturn(false);

		// when
		publicNotificationService.markAsRead(subscriberId, publicNotificationId);

		// then
		verify(publicNotificationPersistencePort, times(1)).saveReceipt(any(PublicNotificationReceipt.class));
	}

	@DisplayName("이미 읽은 공통 알림은 다시 읽음 처리하지 않는다.")
	@Test
	void test2() {
		// given
		Long subscriberId = 1L;
		Long publicNotificationId = 100L;
		when(publicNotificationPersistencePort.existsReceipt(subscriberId, publicNotificationId)).thenReturn(true);

		// when
		publicNotificationService.markAsRead(subscriberId, publicNotificationId);

		// then
		verify(publicNotificationPersistencePort, never()).saveReceipt(any(PublicNotificationReceipt.class));
	}

	@DisplayName("공통 알림 발송 시 구독자를 조회하고 이벤트를 발행한다.")
	@Test
	void test3() {
		// given
		NotificationContext context = new NotificationContext("PAYMENT_CONFIRMED", Map.of("orderId", "123"));
		when(mockUserPersistencePort.findIdsBySubscribedType(any(NotificationType.class), anyLong(), anyInt()))
			.thenReturn(List.of(1L, 2L, 3L));

		// when
		publicNotificationService.pushToPublic(context);

		// then
		verify(mockUserPersistencePort, times(1))
			.findIdsBySubscribedType(any(NotificationType.class), anyLong(), anyInt());
		verify(applicationEventPublisher, times(1))
			.publishEvent(any(PublicNotificationRequestedEvent.class));
	}

	@DisplayName("구독자가 없으면 이벤트를 발행하지 않는다.")
	@Test
	void test4() {
		// given
		NotificationContext context = new NotificationContext("PAYMENT_CONFIRMED", Map.of("orderId", "123"));
		when(mockUserPersistencePort.findIdsBySubscribedType(any(NotificationType.class), anyLong(), anyInt()))
			.thenReturn(List.of());

		// when
		publicNotificationService.pushToPublic(context);

		// then
		verify(applicationEventPublisher, never()).publishEvent(any());
	}
}

