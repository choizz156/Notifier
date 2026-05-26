package io.github.choizz.notifier.adapter;

import java.util.HashMap;
import java.util.Map;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.application.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNotifierAdapter implements NotifierPort {

	private final TemplateRendererPort templateRendererPort;

	@Override
	public void publish(PublicationContext context) {

		try {
			Channel channel = Channel.of(getChannelName().toUpperCase());
			NotificationType type = NotificationType.valueOf(context.notificationType());
			Map<String, String> variables = new HashMap<>(JsonUtils.toMap(context.metadata()));
			variables.put("notificationId", String.valueOf(context.notificationId()));
			String content = templateRendererPort.render(channel, type, variables);
			doSend(context.subscriberId(), content);
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
