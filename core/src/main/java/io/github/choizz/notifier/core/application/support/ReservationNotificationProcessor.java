package io.github.choizz.notifier.core.application.support;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.ReservationNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationNotificationProcessor implements PublishProcessor<ReservationInformation> {

	private final NotificationUseCase notificationUseCase;
	private final ReservationNotificationPersistencePort reservationNotificationPersistencePort;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void process(ReservationInformation notification) {

		NotificationContext context = NotificationContext.builder()
			.subscriberId(notification.subscriberId())
			.notificationType(notification.notificationType())
			.channel(Channel.IN_APP)
			.metadata(Map.of())
			.build();

		notificationUseCase.push(context);

		notification.markAsPublished();
		reservationNotificationPersistencePort.save(notification);

		log.info("예약 알림 발행 성공: notificationId={}, subscriberId={}", notification.id(),
			notification.subscriberId());
	}
}
