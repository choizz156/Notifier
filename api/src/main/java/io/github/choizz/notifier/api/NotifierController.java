package io.github.choizz.notifier.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.api.dto.NotificationCreateWebRequest;
import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.application.port.in.NotificationUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/Notification")
public class NotifierController {

	private final NotificationUseCase notificationUseCase;

	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping
	public void pushEvent(@RequestBody NotificationCreateWebRequest request) {

		NotificationContext context = new NotificationContext(
			request.subscriberId(),
			request.NotificationType(),
			request.channel(),
			request.metadata()
		);

		notificationUseCase.push(context);
	}

	@GetMapping("/{id}/status")
	public NotificationStatusResponse getStatus(@PathVariable("id") Long id) {
		return notificationUseCase.getStatus(id);
	}

	@GetMapping("/subscribers/{subscriberId}")
	public PageResult<NotificationResponse> getNotifications(
		@PathVariable("subscriberId") Long subscriberId,
		@RequestParam(value = "isRead", required = false) Boolean isRead,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size
	) {
		return notificationUseCase.getNotifications(subscriberId, isRead, page, size);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{id}/read")
	public void markAsRead(@PathVariable("id") Long id) {
		notificationUseCase.markAsRead(id);
	}
}
