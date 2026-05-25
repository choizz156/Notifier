package io.github.choizz.notifier.core.application.factory;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationLogFactory {

	public NotificationLog create(EventStatus eventStatus, PublicationContext context) {

		return switch (eventStatus) {
			case SENT -> NotificationLog.sent(context);
			case RETRIED -> NotificationLog.retried(context);
			case FAILED -> NotificationLog.failed(context);
			case REQUESTED, PROCESSING -> throw new IllegalStateException("변경할 이벤트 로그는 REQUESTED 또는 PROCESSING 상태가 아니어야합니다.");
		};
	}
}
