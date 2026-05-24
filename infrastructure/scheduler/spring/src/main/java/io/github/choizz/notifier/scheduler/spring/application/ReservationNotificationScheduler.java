package io.github.choizz.notifier.scheduler.spring.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReservationNotificationScheduler {

	private final ReservationUseCase reservationUseCase;

	@Scheduled(cron = "${scheduler.reservation.cron}")//1 시간마다 실행
	@SchedulerLock(name = "publishReservationNotification", lockAtLeastFor = "30s", lockAtMostFor = "5m")
	public void publishReservationNotification() {
		reservationUseCase.publishReservationNotification();
	}
}
