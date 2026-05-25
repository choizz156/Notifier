package io.github.choizz.notifier.core.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PublicNotificationService implements PublicNotificationUseCase {

	private final PublicNotificationPersistencePort publicNotificationPersistencePort;

	@Override
	@Transactional
	public void markAsRead(Long subscriberId, Long publicNotificationId) {

		boolean alreadyRead = publicNotificationPersistencePort.existsReceipt(subscriberId, publicNotificationId);
		if (!alreadyRead) {
			PublicNotificationReceipt receipt = PublicNotificationReceipt.create(subscriberId, publicNotificationId);
			publicNotificationPersistencePort.saveReceipt(receipt);
			log.info("공통 알림 읽음 처리 완료 - subscriberId: {}, publicNotificationId: {}", subscriberId, publicNotificationId);
		} else {
			log.info("이미 읽음 처리된 공통 알림입니다. - subscriberId: {}, publicNotificationId: {}", subscriberId, publicNotificationId);
		}
	}
}
