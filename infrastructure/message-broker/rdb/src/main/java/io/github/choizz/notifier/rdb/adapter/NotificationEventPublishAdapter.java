package io.github.choizz.notifier.rdb.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.application.port.out.NotificationEventPublisher;

@Component
public class NotificationEventPublishAdapter implements NotificationEventPublisher {

	@Override
	public void publish() {

	}
}
