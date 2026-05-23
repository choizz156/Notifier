package io.github.choizz.notifier.core.application.dto;

import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class PublicationFailContext {

	private final PublishCommandEvent publishCommandEvent;
	private final String failReason;
	private int retryCount;

	public PublicationFailContext(PublishCommandEvent publishCommandEvent, String failReason, int retryCount) {
		this.publishCommandEvent = publishCommandEvent;
		this.failReason = failReason;
		this.retryCount = retryCount;
	}

	public void increaseRetryCount(int retryCount) {
		this.retryCount++;
	}
}
