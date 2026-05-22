package io.github.choizz.notifier.inapp.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.application.port.out.NotifierPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InAppNotifierAdapter implements NotifierPort {

	@Override
	public void publish() {
		// 실제 인앱 알림 발송 로직 대신 로깅으로 대체 (Mocking)
		log.info("[Mock In-App] 인앱 알림 발송 완료");
	}
}
