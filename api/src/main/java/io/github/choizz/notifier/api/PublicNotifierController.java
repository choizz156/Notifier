package io.github.choizz.notifier.api;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/PublicNotification")
public class PublicNotifierController {

	private final PublicNotificationUseCase publicNotificationUseCase;

	@PatchMapping("/{id}/read")
	public void markAsRead(
		@PathVariable("id") Long id,
		@RequestParam("subscriberId") Long subscriberId
	) {
		publicNotificationUseCase.markAsRead(subscriberId, id);
	}
}
