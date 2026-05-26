package io.github.choizz.notifier.admin.application;

import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;

public interface AdminMessageTemplateRepository {
	MessageTemplate save(MessageTemplate template);

	MessageTemplateHistory saveHistory(MessageTemplateHistory history);
}
