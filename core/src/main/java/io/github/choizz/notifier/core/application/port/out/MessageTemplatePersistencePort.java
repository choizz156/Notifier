package io.github.choizz.notifier.core.application.port.out;

import java.util.List;
import java.util.Optional;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public interface MessageTemplatePersistencePort {

	Optional<MessageTemplate> findByChannelAndNotificationType(Channel channel, NotificationType type);

	MessageTemplate findById(Long id);

	List<MessageTemplate> findAll();

	List<MessageTemplateHistory> findHistoriesByTemplateId(Long templateId);
}
