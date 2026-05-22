package io.github.choizz.notifier.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.api.dto.NotificationCreateWebRequest;
import io.github.choizz.notifier.application.port.in.NotificationPushUseCase;
import io.github.choizz.notifier.application.dto.NotificationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/Notification")
public class NotifierController {

	private final NotificationPushUseCase NotificationPushUseCase;

	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping
	public void pushEvent(@RequestBody NotificationCreateWebRequest request) {

		NotificationContext context = new NotificationContext(
			request.subscriberId(),
			request.NotificationType(),
			request.channel(),
			request.metadata()
		);

		NotificationPushUseCase.push(context);
	}
}
