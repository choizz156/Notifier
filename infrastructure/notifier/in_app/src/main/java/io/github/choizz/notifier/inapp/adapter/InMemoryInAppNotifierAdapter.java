package io.github.choizz.notifier.inapp.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.adapter.AbstractNotifierAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InMemoryInAppNotifierAdapter extends AbstractNotifierAdapter {

	@Override
	protected String getChannelName() {
		return "IN_APP";
	}

	@Override
	protected void doSend(Long subscriberId, String content) {
		log.info("[Mock In-App] 수신자: {}, 메시지: {}", subscriberId, content);
	}
}
