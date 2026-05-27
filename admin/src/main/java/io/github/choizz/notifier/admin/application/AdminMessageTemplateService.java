package io.github.choizz.notifier.admin.application;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.out.MessageTemplatePersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminMessageTemplateService {

	private final AdminMessageTemplateRepository adminMessageTemplateRepository;
	private final MessageTemplatePersistencePort messageTemplatePersistencePort;

	@Transactional
	@CacheEvict(value = "messageTemplates", allEntries = true)
	public MessageTemplate create(Channel channel, NotificationType type, String content) {
		messageTemplatePersistencePort.findByChannelAndNotificationType(channel, type)
			.ifPresent(template -> {
				throw new IllegalStateException("이미 해당 채널과 알림 타입에 대한 템플릿이 존재합니다.");
			});

		MessageTemplate newTemplate = MessageTemplate.create(channel, type, content);
		MessageTemplate savedTemplate = adminMessageTemplateRepository.save(newTemplate);

		MessageTemplateHistory history = MessageTemplateHistory.builder()
			.templateId(savedTemplate.id())
			.content(content)
			.build();
		adminMessageTemplateRepository.saveHistory(history);

		return savedTemplate;
	}

	@Transactional
	@CacheEvict(value = "messageTemplates", allEntries = true)
	public MessageTemplate updateContent(Long templateId, String newContent) {
		MessageTemplate template = messageTemplatePersistencePort.findById(templateId);
		template.updateContent(newContent);

		MessageTemplate savedTemplate = adminMessageTemplateRepository.save(template);

		MessageTemplateHistory history = MessageTemplateHistory.builder()
			.templateId(savedTemplate.id())
			.content(newContent)
			.build();
		adminMessageTemplateRepository.saveHistory(history);

		return savedTemplate;
	}
}

