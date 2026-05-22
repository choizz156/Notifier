package io.github.choizz.notifier.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.github.choizz.notifier.application.dto.AlarmContext;
import io.github.choizz.notifier.domain.event.AlarmRequestedEvent;
import io.github.choizz.notifier.domain.model.Alarm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlarmApplicationService implements AlarmPushUseCase {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void push(AlarmContext alarmContext) {
		//1. 알람 엔티티를 저장
		Alarm alarm = Alarm.from(alarmContext);

		//1.1 같은 알람 요청이 있는지 중복 조회해야함
		//TODO: 이벤트가 발행됐으면 -> 핸들러에서 이벤트 발행 사실 저장, 실제 알람 보내야됨.
		applicationEventPublisher.publishEvent(AlarmRequestedEvent.of(alarmContext));
	}
}
