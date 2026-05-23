package io.github.choizz.notifier.core.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.in.NotificationQueryUseCase;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.Notification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService implements NotificationQueryUseCase {

	private final NotificationPersistencePort persistencePort;

	@Override
	public NotificationStatusResponse getStatus(Long notificationId) {
		Notification notification = persistencePort.findById(notificationId);
		return new NotificationStatusResponse(notification.id(), notification.status());
	}

	@Override
	public PageResult<NotificationResponse> getNotifications(Long subscriberId, Boolean isRead, int page, int size) {
		PageResult<Notification> pageResult = persistencePort.findAllBySubscriberId(subscriberId, isRead, page, size);
		
		List<NotificationResponse> responses = pageResult.content().stream()
			.map(NotificationResponse::from)
			.toList();
			
		return new PageResult<>(
			responses,
			pageResult.page(),
			pageResult.size(),
			pageResult.totalElements(),
			pageResult.totalPages()
		);
	}
}
