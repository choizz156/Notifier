package io.github.choizz.notifier.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.api.dto.AlarmCreateWebRequest;
import io.github.choizz.notifier.application.dto.AlarmContext;

@RestController
public class NotifierController {

	@PostMapping("/v1/alarm")
	public void pushEvent(@RequestBody AlarmCreateWebRequest request) {
		AlarmContext context = new AlarmContext(
			request.subscriberId(),
			request.alarmType(),
			request.channel(),
			request.metadata()
		);

	}
}
