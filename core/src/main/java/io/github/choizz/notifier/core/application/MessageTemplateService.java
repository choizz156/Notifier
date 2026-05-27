package io.github.choizz.notifier.core.application;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.in.MessageTemplateUseCase;
import io.github.choizz.notifier.core.application.port.out.MessageTemplatePersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageTemplateService implements MessageTemplateUseCase {

	private final MessageTemplatePersistencePort messageTemplatePersistencePort;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "messageTemplates", key = "#channel.name() + '-' + #type.name()")
	public Optional<MessageTemplate> findActiveTemplate(Channel channel, NotificationType type) {
		return messageTemplatePersistencePort.findByChannelAndNotificationType(channel, type)
			.filter(MessageTemplate::isActive);
	}

	@Override
	@Transactional(readOnly = true)
	public MessageTemplate findById(Long id) {
		return messageTemplatePersistencePort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageTemplate> findAll() {
		return messageTemplatePersistencePort.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageTemplateHistory> findHistories(Long templateId) {
		return messageTemplatePersistencePort.findHistoriesByTemplateId(templateId);
	}

}
