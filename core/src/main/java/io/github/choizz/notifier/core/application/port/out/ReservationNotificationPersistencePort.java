package io.github.choizz.notifier.core.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import io.github.choizz.notifier.core.domain.model.ReservationInformation;

public interface ReservationNotificationPersistencePort {

	ReservationInformation save(ReservationInformation reservationInformation);

	void saveAll(List<ReservationInformation> reservationInformations);

	List<ReservationInformation> findUnpublishedNotificationsBefore(LocalDateTime time, Long lastId, int chunkSize);

	void markAsPublished(List<Long> ids);
}
