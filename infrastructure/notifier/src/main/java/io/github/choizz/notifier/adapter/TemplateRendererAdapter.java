package io.github.choizz.notifier.adapter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.application.port.in.MessageTemplateUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TemplateRendererAdapter implements TemplateRendererPort {

	private final MessageTemplateUseCase messageTemplateUseCase;
	
	private final Map<String, String> staticTemplateCache = new ConcurrentHashMap<>();
	private final Map<Channel, String> baseLayoutCache = new ConcurrentHashMap<>();

	@Override
	public String render(Channel channel, NotificationType type, Map<String, String> metadata) {
		String content;
		try {
			content = messageTemplateUseCase.findActiveTemplate(channel, type)
				.map(MessageTemplate::content)
				.orElseGet(() -> {
					log.debug("[{}] DB에서 활성화된 템플릿을 찾지 못해 정적 파일을 사용합니다. type = {}", channel, type);
					return loadFromClassPath(channel, type);
				});
		} catch (Exception e) {
			log.warn("[{}] 템플릿 로딩 중 오류 발생, 폴백을 시도합니다. error={}", channel, e.getMessage());
			content = loadFromClassPath(channel, type);
		}

		try {
			String renderedContent = replaceVariables(content, metadata);
			return applyBaseLayout(channel, renderedContent);
		} catch (Exception e) {
			log.error("[{}] 템플릿 변수 치환 중 오류 발생", channel, e);
			throw new IllegalStateException("템플릿 변수 치환 중 오류 발생", e);
		}
	}

	private String applyBaseLayout(Channel channel, String content) {
		if (Channel.EMAIL.equals(channel) || Channel.IN_APP.equals(channel)) {
			String baseLayout = loadBaseLayoutFromClassPath(channel);
			return baseLayout.replace("{body_content}", content);
		}
		return content;
	}

	private String loadBaseLayoutFromClassPath(Channel channel) {
		return baseLayoutCache.computeIfAbsent(channel, key -> {
			String templateName = Channel.EMAIL.equals(channel) ? "email-base.html" : "inapp-base.txt";
			String templatePath = "templates/layout/" + templateName;
			ClassPathResource resource = new ClassPathResource(templatePath);

			if (!resource.exists()) {
				log.warn("[{}] 공통 레이아웃을 찾을 수 없습니다: {}", channel, templatePath);
				return "{body_content}";
			}

			try {
				return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				log.error("[{}] 공통 레이아웃 읽기 중 오류 발생: {}", channel, templatePath, e);
				throw new IllegalStateException("공통 레이아웃 읽기 중 오류 발생", e);
			}
		});
	}

	private String loadFromClassPath(Channel channel, NotificationType type) {
		String cacheKey = channel.name() + "-" + type.name();
		return staticTemplateCache.computeIfAbsent(cacheKey, key -> {
			String extension = getExtension(channel);
			String templatePath = "templates/%s.%s".formatted(type.name(), extension);
			ClassPathResource resource = new ClassPathResource(templatePath);

			if (!resource.exists()) {
				log.error("[{}] 정적 템플릿 파일을 찾을 수 없습니다: {}", channel, templatePath);
				throw new IllegalArgumentException("템플릿을 찾을 수 없습니다. type = %s, channel = %s".formatted(type, channel));
			}

			try {
				return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				log.error("[{}] 정적 템플릿 파일 읽기 중 오류 발생: {}", channel, templatePath, e);
				throw new IllegalStateException("정적 템플릿 읽기 중 오류 발생", e);
			}
		});
	}

	private String replaceVariables(String content, Map<String, String> metadata) {

		for (Map.Entry<String, String> entry : metadata.entrySet()) {
			content = content.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return content;
	}

	private String getExtension(Channel channel) {

		return Channel.EMAIL.equals(channel) ? "html" : "txt";
	}
}
