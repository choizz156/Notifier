package io.github.choizz.notifier.scheduler.spring.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReservationNotificationScheduler {

	private final ReservationUseCase reservationUseCase;

	@Scheduled(cron = "0 0 * * * *")
	public void publishReservationNotification(){
		reservationUseCase.publishReservationNotification();
	}

}
