package io.github.choizz.notifier.core.application.port.in;

import java.util.List;
import java.util.Optional;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface MessageTemplateUseCase {

	Optional<MessageTemplate> findActiveTemplate(Channel channel, NotificationType type);

	MessageTemplate findById(Long id);

	List<MessageTemplate> findAll();

	List<MessageTemplateHistory> findHistories(Long templateId);
}
