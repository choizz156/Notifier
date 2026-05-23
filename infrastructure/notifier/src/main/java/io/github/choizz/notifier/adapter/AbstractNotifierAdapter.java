package io.github.choizz.notifier.adapter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNotifierAdapter implements NotifierPort {

	private final TemplateRendererPort templateRendererPort;

	@Override
	public void publish(PublishCommandEvent event) {

		try {
			Channel channel = Channel.valueOf(getChannelName().toUpperCase());
			NotificationType type = NotificationType.valueOf(event.notificationType());
			String content = templateRendererPort.render(channel, type, event.metadata());
			doSend(event.subscriberId(), content);
		} catch (Exception e) {
			log.error("[{}] 템플릿 처리 중 오류 발생", getChannelName(), e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean supports(String channel) {

		return getChannelName().equalsIgnoreCase(channel);
	}

	protected abstract String getChannelName();

	protected String getTemplateExtension() {
		return "txt";
	}

	protected abstract void doSend(Long subscriberId, String content);
}
