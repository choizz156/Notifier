package io.github.choizz.notifier.core.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationEventLogUseCase;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import io.github.choizz.notifier.core.domain.event.PublishCompletedEvent;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationCompletedEventHandler {

	private final NotificationEventLogUseCase notificationEventLogUseCase;
	private final NotificationUseCase notificationUseCase;
	
	@Async("taskExecutor")
	@Transactional
	@EventListener
	public void handleNotificationCompleted(PublishCompletedEvent event) {
		try {
			notificationUseCase.updateStatus(event.notificationId(), NotificationStatus.COMPLETED);
			notificationEventLogUseCase.saveEventLog(event.notificationId(), EventStatus.SENT, PublicationContext.success(event));
		} catch (Exception e) {
			log.error("알림(ID: {}) 상태 및 이벤트 로그 성공 업데이트에 실패하여 상태가 변경되지 않았습니다.", event.notificationId(), e);
			throw new RuntimeException(e);
		}
	}
}
