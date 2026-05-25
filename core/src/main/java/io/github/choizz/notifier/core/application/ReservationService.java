package io.github.choizz.notifier.core.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;
import io.github.choizz.notifier.core.application.port.out.ReservationNotificationPersistencePort;
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService implements ReservationUseCase {

	private final ReservationNotificationPersistencePort reservationNotificationPersistencePort;
	private final NotificationUseCase notificationUseCase;

	@Override
	@Transactional
	public void reserve(List<Long> subscriberIds, NotificationType type, LocalDateTime reservationTime) {

		log.info("예약 알림 생성 요청: subscriberCount={}, type={}, reservationTime={}",
			subscriberIds.size(),
			type,
			reservationTime
		);

		List<ReservationInformation> reservationInformations = subscriberIds.stream()
			.map(subscriberId -> ReservationInformation.of(subscriberId, type, reservationTime))
			.toList();

		reservationNotificationPersistencePort.saveAll(reservationInformations);
	}

	@Override
	public void publishReservationNotification() {

		LocalDateTime now = LocalDateTime.now();
		log.info("예약 알림 발행 시작: 기준 시간={}", now);

		ChunkExecutor.execute(
			0L,
			ReservationInformation::id,
			lastId ->
				reservationNotificationPersistencePort.findUnpublishedNotificationsBefore(
					now,
					lastId,
					ChunkExecutor.CHUNK_SIZE
				),
			this::publishChunk
		);
	}

	private void publishChunk(List<ReservationInformation> chunk) {

		log.info("예약 알림 Chunk 발행 처리: size={}", chunk.size());
		List<Long> successIds = new ArrayList<>();

		for (ReservationInformation notification : chunk) {
			try {
				NotificationContext context = NotificationContext.builder()
					.subscriberId(notification.subscriberId())
					.notificationType(notification.notificationType())
					.metadata(Map.of())
					.build();

				notificationUseCase.push(context);
				successIds.add(notification.id());
			} catch (Exception e) {
				log.warn("예약 알림 발행 실패: referencedId={}, subscriberId={}", notification.id(),
					notification.subscriberId(), e);
			}
		}

		if (!successIds.isEmpty()) {
			reservationNotificationPersistencePort.markAsPublished(successIds);
		}
	}
}
