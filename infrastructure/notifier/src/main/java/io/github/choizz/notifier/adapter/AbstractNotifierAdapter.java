package io.github.choizz.notifier.adapter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import io.github.choizz.notifier.application.port.out.NotifierPort;
import io.github.choizz.notifier.domain.event.NotificationRequestedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractNotifierAdapter implements NotifierPort {

	@Override
	public void publish(NotificationRequestedEvent event) {
		try {
			String templatePath = "templates/%s.txt".formatted(event.notificationType());
			ClassPathResource resource = new ClassPathResource(templatePath);
			if (!resource.exists()) {
				log.error("[{}] 템플릿을 찾을 수 없습니다: {}", getChannelName(), templatePath);
				throw new IllegalArgumentException("템플릿을 찾을 수 없습니다. id = %s, type = %s"
					.formatted(event.notificationType(), event.notificationType()));
			}
			
			String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			
			for (Map.Entry<String, String> entry : event.metadata().entrySet()) {
				content = content.replace("{" + entry.getKey() + "}", entry.getValue());
			}

			doSend(event.subscriberId(), content);
		} catch (Exception e) {
			log.error("[{}] 템플릿 처리 중 오류 발생", getChannelName(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean supports(String channel) {
		return getChannelName().equalsIgnoreCase(channel);
	}

	protected abstract String getChannelName();

	protected abstract void doSend(Long subscriberId, String content);
}
