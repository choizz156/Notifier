package io.github.choizz.notifier.core.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;

@ExtendWith(MockitoExtension.class)
class PublicNotificationServiceTest {

	@Mock
	private PublicNotificationPersistencePort publicNotificationPersistencePort;

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
}
