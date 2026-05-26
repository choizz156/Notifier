package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;

public interface PublicNotificationPersistencePort {
	void saveReceipt(PublicNotificationReceipt receipt);
	boolean existsReceipt(Long subscriberId, Long publicNotificationId);
	PublicNotification save(PublicNotification publicNotification);
	PublicNotification findById(Long id);
	void update(PublicNotification publicNotification);
}

