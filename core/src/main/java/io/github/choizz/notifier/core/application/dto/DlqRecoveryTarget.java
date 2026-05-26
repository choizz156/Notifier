package io.github.choizz.notifier.core.application.dto;

import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import lombok.Builder;

@Builder
public record DlqRecoveryTarget(
	Long dlqId,
	PublicNotificationRequestedEvent event
) {
}
