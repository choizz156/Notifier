package io.github.choizz.notifier.core.application;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PublicNotificationService implements PublicNotificationUseCase {

	private final PublicNotificationPersistencePort publicNotificationPersistencePort;
	private final MockUserPersistencePort mockUserPersistencePort;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final NotificationLogPersistencePort notificationLogPersistencePort;

	@Override
	@Transactional
	public void markAsRead(Long subscriberId, Long publicNotificationId) {

		boolean alreadyRead = publicNotificationPersistencePort.existsReceipt(subscriberId, publicNotificationId);
		if (alreadyRead) {
			log.info("이미 읽음 처리된 공통 알림입니다. - subscriberId: {}, publicNotificationId: {}",
				subscriberId,
				publicNotificationId
			);
			return;
		}

		PublicNotificationReceipt receipt = PublicNotificationReceipt.of(subscriberId, publicNotificationId);
		publicNotificationPersistencePort.saveReceipt(receipt);
		log.info("공통 알림 읽음 처리 완료 - subscriberId: {}, publicNotificationId: {}", subscriberId, publicNotificationId);
	}

	@Override
	public void pushToPublic(NotificationContext context) {

		String metadata = context.metadataToJson();
		String notificationType = context.notificationType().name();
		String idempotentKey = UUID.randomUUID().toString();

		ChunkExecutor.execute(
			0L,
			(Long id) -> id,
			lastId -> mockUserPersistencePort.findIdsBySubscribedType(
				context.notificationType(),
				lastId,
				ChunkExecutor.CHUNK_SIZE
			),
			subscriberIds -> applicationEventPublisher.publishEvent(
				new PublicNotificationRequestedEvent(subscriberIds, metadata, notificationType, idempotentKey)
			)
		);
	}

	@Override
	@Transactional
	public void completeIfAllDone(Long publicNotificationId) {
		long total = notificationLogPersistencePort.countByReferenceIdAndReferenceType(
			publicNotificationId, ReferenceType.PUBLIC
		);
		long terminated = notificationLogPersistencePort.countTerminatedByReferenceIdAndReferenceType(
			publicNotificationId, ReferenceType.PUBLIC
		);

		if (total > 0 && total == terminated) {
			PublicNotification publicNotification = publicNotificationPersistencePort.findById(publicNotificationId);
			publicNotification.markAsCompleted();
			publicNotificationPersistencePort.update(publicNotification);
			log.info("공개 알림 전체 발송 완료 - publicNotificationId: {}", publicNotificationId);
		}
	}
}
