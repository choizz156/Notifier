package io.github.choizz.notifier.persistence.jpa.adapter;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationEntity;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationReceiptEntity;
import io.github.choizz.notifier.persistence.jpa.repository.PublicNotificationJpaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
	@Transactional
	public PublicNotification save(PublicNotification publicNotification) {
		PublicNotificationEntity entity = new PublicNotificationEntity(
			publicNotification.notificationType(),
			publicNotification.metadata(),
			publicNotification.idempotencyKey(),
			publicNotification.status()
		);
		entity.version(publicNotification.version());
		PublicNotificationEntity savedEntity = publicNotificationJpaRepository.save(entity);
		return toDomain(savedEntity);
	}

	@Override
	public PublicNotification findById(Long id) {
		PublicNotificationEntity entity = publicNotificationJpaRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("공개 알림을 찾을 수 없습니다. id=" + id));
		return toDomain(entity);
	}

	@Override
	@Transactional
	public void update(PublicNotification publicNotification) {
		PublicNotificationEntity entity = publicNotificationJpaRepository.findById(publicNotification.id())
			.orElseThrow(() -> new NoSuchElementException("공개 알림을 찾을 수 없습니다. id=" + publicNotification.id()));
		entity.updateStatus(publicNotification.status());
	}

	private PublicNotification toDomain(PublicNotificationEntity entity) {
		return PublicNotification.builder()
			.id(entity.id())
			.notificationType(entity.notificationType())
			.metadata(entity.metadata())
			.idempotencyKey(entity.idempotencyKey())
			.status(entity.status())
			.createdAt(entity.createdAt())
			.updatedAt(entity.updatedAt())
			.version(entity.version())
			.build();
	}
}

