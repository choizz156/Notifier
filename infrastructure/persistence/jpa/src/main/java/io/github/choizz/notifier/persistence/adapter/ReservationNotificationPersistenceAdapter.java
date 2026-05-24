package io.github.choizz.notifier.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.ReservationNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import io.github.choizz.notifier.persistence.entity.ReservationNotificationEntity;
import io.github.choizz.notifier.persistence.repository.ReservationNotificationJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationNotificationPersistenceAdapter implements ReservationNotificationPersistencePort {

	private final ReservationNotificationJpaRepository reservationNotificationJpaRepository;

	@Override
	public ReservationInformation save(ReservationInformation reservationInformation) {
		ReservationNotificationEntity entity = ReservationNotificationMapper.toEntity(reservationInformation);
		ReservationNotificationEntity savedEntity = reservationNotificationJpaRepository.save(entity);
		return ReservationNotificationMapper.toDomain(savedEntity);
	}

	@Override
	public void saveAll(List<ReservationInformation> reservationInformations) {
		List<ReservationNotificationEntity> entities = reservationInformations.stream()
			.map(ReservationNotificationMapper::toEntity)
			.toList();
		reservationNotificationJpaRepository.saveAll(entities);
	}

	@Override
	public List<ReservationInformation> findUnpublishedNotificationsBefore(LocalDateTime time, Long lastId, int chunkSize) {
		return reservationNotificationJpaRepository.findUnpublishedBefore(time, lastId, Limit.of(chunkSize))
			.stream()
			.map(ReservationNotificationMapper::toDomain)
			.toList();
	}

	@Override
	public void markAsPublished(List<Long> ids) {
		reservationNotificationJpaRepository.markAsPublished(ids);
	}
}
