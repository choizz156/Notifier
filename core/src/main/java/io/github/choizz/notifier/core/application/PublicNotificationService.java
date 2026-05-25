package io.github.choizz.notifier.core.application;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PublicNotificationService implements PublicNotificationUseCase {

	private final PublicNotificationPersistencePort publicNotificationPersistencePort;
	private final MockUserPersistencePort mockUserPersistencePort;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	@Transactional
	public void markAsRead(Long subscriberId, Long publicNotificationId) {

		boolean alreadyRead = publicNotificationPersistencePort.existsReceipt(subscriberId, publicNotificationId);
		if (!alreadyRead) {
			PublicNotificationReceipt receipt = PublicNotificationReceipt.create(subscriberId, publicNotificationId);
			publicNotificationPersistencePort.saveReceipt(receipt);
			log.info("공통 알림 읽음 처리 완료 - subscriberId: {}, publicNotificationId: {}", subscriberId, publicNotificationId);
		} else {
			log.info("이미 읽음 처리된 공통 알림입니다. - subscriberId: {}, publicNotificationId: {}", subscriberId,
				publicNotificationId);
		}
	}

	@Override
	public void pushBulk(NotificationContext context) {

		PublicNotification savedPublicNotification = publicNotificationPersistencePort.save(
			PublicNotification.of(context.notificationType(), context.metadataToJson())
		);

		List<Long> subscriberIds = mockUserPersistencePort.findIdsBySubscribedType(
			context.notificationType()
		);

		for (Long subscriberId : subscriberIds) {
			Set<Channel> subscribedChannels = mockUserPersistencePort.findSubscribedChannels(subscriberId);

			for (Channel channel : subscribedChannels) {
				applicationEventPublisher.publishEvent(
					PublicNotificationRequestedEvent.of(savedPublicNotification, context, channel.name(), subscriberId)
				);
			}
		}
	}
}
