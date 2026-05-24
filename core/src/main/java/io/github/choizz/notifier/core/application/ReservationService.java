package io.github.choizz.notifier.core.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;
import io.github.choizz.notifier.core.application.port.out.ReservationNotificationPersistencePort;
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService implements ReservationUseCase {

	private final ReservationNotificationPersistencePort reservationNotificationPersistencePort;
	private final NotificationUseCase notificationUseCase;

	@Override
	public void reserve(List<Long> subscriberIds, NotificationType type, LocalDateTime reservationTime) {

		log.info("예약 알림 생성 요청: subscriberCount={}, type={}, reservationTime={}", subscriberIds.size(), type,
			reservationTime);

		for (Long subscriberId : subscriberIds) {
			ReservationInformation reservationInformation = ReservationInformation.of(subscriberId, type,
				reservationTime);
			reservationNotificationPersistencePort.save(reservationInformation);
		}
	}

	@Override
	public void publishReservationNotification() {

		LocalDateTime now = LocalDateTime.now();
		log.info("예약 알림 발행 시작: 기준 시간={}", now);

		ChunkExecutor.execute(
			0L,
			ReservationInformation::id,
			lastId ->
				reservationNotificationPersistencePort.findUnpublishedNotificationsBefore(now, lastId, ChunkExecutor.CHUNK_SIZE),
			this::publishChunk
		);
	}

	private void publishChunk(List<ReservationInformation> chunk) {

		log.info("예약 알림 Chunk 발행 처리: size={}", chunk.size());
		for (ReservationInformation notification : chunk) {
			try {
				publish(notification);
				notification.markAsPublished();
				reservationNotificationPersistencePort.save(notification);
				log.info("예약 알림 발행 성공: notificationId={}, subscriberId={}", notification.id(),
					notification.subscriberId());
			} catch (Exception e) {
				log.warn("예약 알림 발행 실패: notificationId={}, subscriberId={}", notification.id(),
					notification.subscriberId(), e);
			}
		}
	}

	private void publish(ReservationInformation notification) {

		NotificationContext context = NotificationContext.builder()
			.subscriberId(notification.subscriberId())
			.notificationType(notification.notificationType())
			.channel(Channel.IN_APP)
			.metadata(Map.of())
			.build();

		notificationUseCase.push(context);
	}
}
