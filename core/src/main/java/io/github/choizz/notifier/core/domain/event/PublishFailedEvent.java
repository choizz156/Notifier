package io.github.choizz.notifier.core.domain.event;

import io.github.choizz.notifier.core.application.dto.PublicationContext;

public record PublishFailedEvent(
	PublicationContext context
) {

}
