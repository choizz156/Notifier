package io.github.choizz.notifier.core.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface ReservationUseCase {

	void reserve(List<Long> subscriberIds, NotificationType type, LocalDateTime reservationTime);

	void publishReservationNotification();
}
