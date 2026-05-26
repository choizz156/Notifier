package io.github.choizz.notifier.persistence.jpa.adapter;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.out.LoadCombinedNotificationPort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.persistence.jpa.repository.CombinedNotificationJpaRepository;
import io.github.choizz.notifier.persistence.jpa.repository.CombinedNotificationProjection;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CombinedNotificationPersistenceAdapter implements LoadCombinedNotificationPort {

	private final CombinedNotificationJpaRepository combinedNotificationJpaRepository;

	@Override
	public PageResult<NotificationResponse> loadCombinedNotifications(Long subscriberId, Boolean isRead, int page, int size) {
		
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CombinedNotificationProjection> combinedPage;

		if (isRead != null) {
			combinedPage = combinedNotificationJpaRepository.findCombinedNotificationsByIsRead(subscriberId, isRead, pageRequest);
		} else {
			combinedPage = combinedNotificationJpaRepository.findCombinedNotifications(subscriberId, pageRequest);
		}

		List<NotificationResponse> content = combinedPage.getContent().stream()
			.map(this::mapProjectionToResponse)
			.toList();

		return new PageResult<>(content, page, size, combinedPage.getTotalElements(), combinedPage.getTotalPages());
	}

	private NotificationResponse mapProjectionToResponse(CombinedNotificationProjection projection) {
		String channelStr = projection.getChannel();
		Channel channel = null;
		try {
			if (!"NONE".equals(channelStr) && channelStr != null) {
				channel = Channel.of(channelStr);
			}
		} catch (IllegalArgumentException ignored) {}

		NotificationType notificationType = NotificationType.valueOf(projection.getNotification_type());
		
		return new NotificationResponse(
			projection.getId(),
			ReferenceType.valueOf(projection.getReference_type()),
			projection.getSubscriber_id(),
			notificationType,
			channel,
			NotificationStatus.valueOf(projection.getStatus()),
			notificationType.title(), 
			projection.getIs_read(),
			projection.getCreated_at(),
			projection.getManual_retry_count()
		);
	}
}
