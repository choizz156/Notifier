package io.github.choizz.notifier.core.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.DlqPort;
import io.github.choizz.notifier.core.application.support.PublicNotificationBulkProcessor;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PublicNotificationRequestedEventHandler {

	private final PublicNotificationBulkProcessor publicNotificationBulkProcessor;
	private final DlqPort dlqPort;

	@Async("taskExecutor")
	@EventListener
	public void handle(PublicNotificationRequestedEvent event) {
		try {
			publicNotificationBulkProcessor.chunkToPublic(event);
		} catch (Exception e) {
			log.error("비동기 처리 중 에러 발생, DLQ 적재 시작 - 이벤트 ID: {}", event.idempotentKey(), e);
			dlqPort.saveDlq(event, e);
		}
	}

}
