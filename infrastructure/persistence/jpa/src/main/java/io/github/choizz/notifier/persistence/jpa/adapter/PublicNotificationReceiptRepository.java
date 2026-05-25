package io.github.choizz.notifier.persistence.jpa.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationReceiptEntity;

public interface PublicNotificationReceiptRepository extends JpaRepository<PublicNotificationReceiptEntity, Long> {
	boolean existsBySubscriberIdAndPublicNotificationId(Long subscriberId, Long publicNotificationId);
}
