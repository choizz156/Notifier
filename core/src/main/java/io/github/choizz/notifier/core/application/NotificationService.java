package io.github.choizz.notifier.core.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.dto.NotificationDetailResponse;
import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.LoadCombinedNotificationPort;
import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.application.support.ChunkExecutor;
import io.github.choizz.notifier.core.application.support.NotificationRetryProcessor;
import io.github.choizz.notifier.core.domain.event.NotificationRequestedEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService implements NotificationUseCase {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final NotificationPersistencePort notificationPersistencePort;
	private final NotificationLogUseCase notificationLogUseCase;
	private final TemplateRendererPort templateRendererPort;
	private final NotificationRetryProcessor notificationRetryProcessor;
	private final MockUserPersistencePort mockUserPersistencePort;
	private final LoadCombinedNotificationPort loadCombinedNotificationPort;

	@Override
	@Transactional
	public void push(NotificationContext notificationContext) {

		boolean isSubscribedToType = mockUserPersistencePort.isSubscribed(
			notificationContext.subscriberId(),
			notificationContext.notificationType()
		);

		if (!isSubscribedToType) {
			log.info("유저가 해당 알림 타입을 수신 거부했습니다. subscriberId = {}, type = {}",
				notificationContext.subscriberId(), notificationContext.notificationType());
			return;
		}

		Set<Channel> activeChannels = mockUserPersistencePort.findSubscribedChannels(
			notificationContext.subscriberId()
		);

		if (activeChannels.isEmpty()) {
			log.info("유저가 켜둔 알림 채널이 없습니다. subscriberId = {}", notificationContext.subscriberId());
			return;
		}

		List<Notification> notificationsToSave = addNotifications(notificationContext, activeChannels);

		saveAndPush(notificationContext, notificationsToSave);
	}

	@Override
	@Transactional
	public void markAsRead(Long notificationId) {

		notificationPersistencePort.markAsRead(notificationId);
	}

	@Override
	@Transactional
	public void updateStatus(Long notificationId, NotificationStatus status) {

		Notification notification = notificationPersistencePort.findById(notificationId);

		switch (status) {
			case COMPLETED -> notification.markAsCompleted();
			case RETRYING -> notification.markAsRetrying();
		}

		notificationPersistencePort.save(notification);
	}

	@Override
	@Transactional
	public void fail(Long notificationId, String failReason) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		notification.markAsFailed(failReason);
		notificationPersistencePort.save(notification);
	}

	@Override
	public void retry() {

		ChunkExecutor.execute(
			0L,
			id -> id,
			lastId -> notificationLogUseCase.findUnprocessedNotificationIds(lastId, ChunkExecutor.CHUNK_SIZE),
			notificationRetryProcessor::processChunk
		);
	}

	@Override
	public void retryStuckNotification(List<Long> notificationId) {

		notificationRetryProcessor.processChunk(notificationId);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificationStatusResponse findStatus(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		return new NotificationStatusResponse(notification.id(), notification.status());
	}

	@Override
	@Transactional(readOnly = true)
	public PageResult<NotificationResponse> findNotifications(Long subscriberId, Boolean isRead, int page, int size) {

		return loadCombinedNotificationPort.loadCombinedNotifications(subscriberId, isRead, page, size);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificationDetailResponse findNotificationDetail(Long notificationId) {

		Notification notification = notificationPersistencePort.findById(notificationId);
		String content = templateRendererPort.render(
			notification.channel(),
			notification.notificationType(),
			JsonUtils.toMap(notification.metadata())
		);
		return NotificationDetailResponse.of(notification, content);
	}

	private List<Notification> addNotifications(NotificationContext notification,
		Set<Channel> activeChannels
	) {

		List<Notification> notificationsToSave = new ArrayList<>();
		for (Channel channel : activeChannels) {
			boolean isDuplicate = notificationPersistencePort.existsDuplicate(
				notification.subscriberId(),
				notification.notificationType(),
				channel
			);

			if (isDuplicate) {
				log.warn("이미 처리 중인 동일한 알람이 존재합니다. id = {}, type = {}, channel = {}",
					notification.subscriberId(), notification.notificationType(), channel);
				continue;
			}

			notificationsToSave.add(Notification.of(notification, channel));
		}
		return notificationsToSave;
	}

	private void saveAndPush(NotificationContext notificationContext, List<Notification> notificationsToSave) {

		if (notificationsToSave.isEmpty()) {
			log.info("저장할 Notification 객체가 없습니다. size = {}", 0);
			return;
		}

		List<NotificationLog> notificationLogs = new ArrayList<>();
		notificationsToSave.forEach(notification ->
			notificationLogs.add(NotificationLog.request(notification))
		);

		notificationLogUseCase.saveAll(notificationLogs);
		notificationPersistencePort.saveAll(notificationsToSave)
			.forEach(savedNotification ->
				applicationEventPublisher.publishEvent(
					NotificationRequestedEvent.of(savedNotification, notificationContext))
			);
	}
}
