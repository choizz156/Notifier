package io.github.choizz.notifier.adapter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TemplateRendererAdapter implements TemplateRendererPort {

	@Override
	public String render(Channel channel, NotificationType type, Map<String, String> metadata) {

		String extension = getExtension(channel);
		String templatePath = "templates/%s.%s".formatted(type.name(), extension);
		ClassPathResource resource = new ClassPathResource(templatePath);

		if (!resource.exists()) {
			log.error("[{}] 템플릿을 찾을 수 없습니다: {}", channel, templatePath);
			throw new IllegalArgumentException("템플릿을 찾을 수 없습니다. type = %s, channel = %s".formatted(type, channel));
		}

		try {
			String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			return replaceVariables(content, metadata);
		} catch (Exception e) {
			log.error("[{}] 템플릿 처리 중 오류 발생", channel, e);
			throw new IllegalStateException("템플릿 처리 중 오류 발생", e);
		}
	}

	private String replaceVariables(String content, Map<String, String> metadata) {

		for (Map.Entry<String, String> entry : metadata.entrySet()) {
			content = content.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return content;
	}

	private String getExtension(Channel channel) {

		return channel == Channel.EMAIL ? "html" : "txt";
	}
}
