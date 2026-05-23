package io.github.choizz.notifier.core.application.port.out;

import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.Channel;

public interface NotificationPersistencePort {

	Notification save(Notification notification);
	
	void markAsRead(Long id);

	Notification findById(Long id);

	Notification findBySubscriberId(Long subscriberId);

	boolean existsDuplicate(Long subscriberId, NotificationType notificationType, Channel channel);

	PageResult<Notification> findAllBySubscriberId(Long subscriberId, Boolean isRead, int page, int size);
}
