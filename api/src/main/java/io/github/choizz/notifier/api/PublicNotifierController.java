package io.github.choizz.notifier.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.api.dto.PublicNotificationWebRequest;
import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notification/public")
public class PublicNotifierController {

	private final PublicNotificationUseCase publicNotificationUseCase;

	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping
	public void pushBulkEvent(
		@RequestBody PublicNotificationWebRequest request
	) {

		NotificationContext notificationContext = new NotificationContext(request.type(), request.metadata());
		publicNotificationUseCase.pushToPublic(notificationContext);
	}

	@PatchMapping("/{id}/read")
	public void markAsRead(
		@PathVariable("id") Long id,
		@RequestParam("subscriberId") Long subscriberId
	) {

		publicNotificationUseCase.markAsRead(subscriberId, id);
	}
}
