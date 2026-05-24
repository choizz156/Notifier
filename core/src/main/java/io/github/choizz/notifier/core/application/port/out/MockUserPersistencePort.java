package io.github.choizz.notifier.core.application.port.out;

import java.util.List;

import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface MockUserPersistencePort {
	boolean isSubscribed(Long userId, NotificationType type);
	List<NotificationType> findSubscribedTypes(Long userId);
}
