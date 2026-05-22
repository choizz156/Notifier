package io.github.choizz.notifier.email.adapter;

import org.springframework.stereotype.Component;

import io.github.choizz.notifier.application.port.out.NotifierPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailNotifierAdapter implements NotifierPort {

	@Override
	public void publish() {
		// 실제 이메일 발송 로직 대신 로깅으로 대체 (Mocking)
		log.info("[Mock Email] 이메일 알림 발송 완료");
	}
}
