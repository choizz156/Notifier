package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.model.Notification;
import io.github.choizz.notifier.domain.model.NotificationStatus;
import io.github.choizz.notifier.domain.model.NotificationType;
import io.github.choizz.notifier.domain.model.Channel;

public interface NotificationPersistencePort {

	Notification save(Notification notification);

	void updateStatus(long id, NotificationStatus notificationStatus);

	Notification findById(Long id);

	Notification findBySubscriberId(Long subscriberId);

	boolean existsDuplicate(Long subscriberId, NotificationType notificationType, Channel channel);
}
