package io.github.choizz.notifier.core.application.factory;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationEventLogFactory {

	public NotificationEventLog create(EventStatus eventStatus, PublicationContext context) {

		return switch (eventStatus) {
			case SENT -> NotificationEventLog.sent(
				context.notificationId(),
				Channel.valueOf(context.channel()),
				context.metadata(),
				context.retryCount()
			);
			case RETRIED -> NotificationEventLog.retried(
				context.notificationId(),
				Channel.valueOf(context.channel()),
				context.failReason(),
				context.metadata(),
				context.retryCount()
			);
			case FAILED -> NotificationEventLog.failed(
				context.notificationId(),
				Channel.valueOf(context.channel()),
				context.failReason(),
				context.metadata(),
				context.retryCount()
			);
			case REQUESTED, PROCESSING -> throw new IllegalStateException("변경할 이벤트 로그는 REQUESTED 또는 PROCESSING 상태가 아니어야합니다.");
		};
	}
}
