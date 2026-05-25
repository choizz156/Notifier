package io.github.choizz.notifier.scheduler.spring.application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;

@ExtendWith(MockitoExtension.class)
class ReservationNotificationSchedulerTest {

	@Mock
	private ReservationUseCase reservationUseCase;

	@InjectMocks
	private ReservationNotificationScheduler scheduler;

	@DisplayName("예약된 알림 발행을 스케줄링한다.")
	@Test
	void test1() {
		// when
		scheduler.publishReservationNotification();

		// then
		verify(reservationUseCase, times(1)).publishReservationNotification();
	}
}
