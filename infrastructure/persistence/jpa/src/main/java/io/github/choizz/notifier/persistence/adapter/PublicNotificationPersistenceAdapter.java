package io.github.choizz.notifier.persistence.adapter;

import org.springframework.stereotype.Repository;

import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import io.github.choizz.notifier.persistence.entity.PublicNotificationReceiptEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PublicNotificationPersistenceAdapter implements PublicNotificationPersistencePort {

	private final PublicNotificationReceiptRepository publicNotificationReceiptRepository;

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
}
