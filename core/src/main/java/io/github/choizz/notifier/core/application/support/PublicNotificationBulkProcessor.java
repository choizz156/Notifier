package io.github.choizz.notifier.core.application.support;

import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.PublicNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PublicNotificationBulkProcessor {

	private final MockUserPersistencePort mockUserPersistencePort;
	private final PublicNotificationPersistencePort publicNotificationPersistencePort;
	private final NotificationLogUseCase notificationLogUseCase;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void chunkToPublic(PublicNotificationRequestedEvent event) {

		PublicNotification publicNotification = publicNotificationPersistencePort.save(PublicNotification.of(event));

		for (Long subscriberId : event.subscriberIds()) {
			Set<Channel> subscribedChannels = mockUserPersistencePort.findSubscribedChannels(subscriberId);

			if (subscribedChannels.isEmpty()) {
				continue;
			}

			subscribedChannels.forEach(channel -> {
					notificationLogUseCase.save(NotificationLog.requestToPublic(publicNotification, channel, subscriberId));
					applicationEventPublisher.publishEvent(
						PublishCommandEvent.toPublic(publicNotification, subscriberId, channel.name())
					);
				}
			);
		}
	}
}
