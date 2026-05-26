package io.github.choizz.notifier.admin.dto;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;

public record MessageTemplateCreateRequest(
	Channel channel,
	NotificationType type,
	String content
) {
}
