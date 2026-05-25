package io.github.choizz.notifier.core.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.support.PublicNotificationBulkProcessor;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PublicNotificationRequestedEventHandler {

	private final PublicNotificationBulkProcessor publicNotificationBulkProcessor;

	@Async("taskExecutor")
	@EventListener
	public void handle(PublicNotificationRequestedEvent event) {
		publicNotificationBulkProcessor.chunkToPublic(event);
	}

}
