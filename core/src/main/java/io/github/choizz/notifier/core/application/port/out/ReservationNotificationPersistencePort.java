package io.github.choizz.notifier.core.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import io.github.choizz.notifier.core.domain.model.ReservationInformation;

public interface ReservationNotificationPersistencePort {

	ReservationInformation save(ReservationInformation reservationInformation);

	List<ReservationInformation> findUnpublishedNotificationsBefore(LocalDateTime time, Long lastId, int chunkSize);
}
