package io.github.choizz.notifier.core.application.port.out;

import java.util.List;
import java.util.Set;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface MockUserPersistencePort {
	boolean isSubscribed(Long userId, NotificationType type);
	List<NotificationType> findSubscribedTypes(Long userId);
	Set<Channel> findSubscribedChannels(Long userId);
}
