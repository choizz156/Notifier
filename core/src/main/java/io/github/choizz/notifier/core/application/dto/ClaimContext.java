package io.github.choizz.notifier.core.application.dto;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

public record ClaimContext(
	Long notificationId,
	ReferenceType referenceType,
	Channel channel,
	Long subscriberId
) {
}
