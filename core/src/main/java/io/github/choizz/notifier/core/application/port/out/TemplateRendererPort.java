package io.github.choizz.notifier.core.application.port.out;

import java.util.Map;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface TemplateRendererPort {
	String render(Channel channel, NotificationType type, Map<String, String> metadata);
}
