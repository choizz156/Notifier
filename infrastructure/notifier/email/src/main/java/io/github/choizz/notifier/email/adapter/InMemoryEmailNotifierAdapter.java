package io.github.choizz.notifier.email.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.adapter.AbstractNotifierAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InMemoryEmailNotifierAdapter extends AbstractNotifierAdapter {

	@Override
	protected String getChannelName() {
		return "EMAIL";
	}

	@Override
	protected void doSend(Long subscriberId, String content) {
		log.info("[Mock Email] 수신자: {}, 메시지: {}", subscriberId, content);
	}
}
