package io.github.choizz.notifier.persistence.adapter;

import org.springframework.stereotype.Repository;

import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import io.github.choizz.notifier.persistence.entity.PublicNotificationEntity;
import io.github.choizz.notifier.persistence.entity.PublicNotificationReceiptEntity;
import io.github.choizz.notifier.persistence.repository.PublicNotificationJpaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PublicNotificationPersistenceAdapter implements PublicNotificationPersistencePort {

	private final PublicNotificationReceiptRepository publicNotificationReceiptRepository;
	private final PublicNotificationJpaRepository publicNotificationJpaRepository;

	@Override
	public void saveReceipt(PublicNotificationReceipt receipt) {
		PublicNotificationReceiptEntity entity = PublicNotificationReceiptEntity.builder()
			.subscriberId(receipt.subscriberId())
			.publicNotificationId(receipt.publicNotificationId())
			.build();
		publicNotificationReceiptRepository.save(entity);
	}

	@Override
	public boolean existsReceipt(Long subscriberId, Long publicNotificationId) {
		return publicNotificationReceiptRepository.existsBySubscriberIdAndPublicNotificationId(subscriberId, publicNotificationId);
	}

	@Override
	public PublicNotification save(PublicNotification publicNotification) {
		PublicNotificationEntity entity = PublicNotificationEntity.of(
			publicNotification.notificationType(),
			publicNotification.metadata()
		);
		PublicNotificationEntity savedEntity = publicNotificationJpaRepository.save(entity);

		return PublicNotification.builder()
			.id(savedEntity.id())
			.notificationType(savedEntity.notificationType())
			.metadata(savedEntity.metadata())
			.createdAt(savedEntity.createdAt())
			.updatedAt(savedEntity.updatedAt())
			.build();
	}
}
