package io.github.choizz.notifier.core.application.port.in;

import java.time.LocalDateTime;
import java.util.Map;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface ReservationUseCase {

	void reservePublic(NotificationType type, Map<String, String> metadata, LocalDateTime reservationTime);

	void publishReservationNotification();
}
