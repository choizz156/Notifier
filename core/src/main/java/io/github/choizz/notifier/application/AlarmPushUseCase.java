package io.github.choizz.notifier.application;

import io.github.choizz.notifier.application.dto.AlarmContext;

public interface AlarmPushUseCase {

	void push(AlarmContext alarmContext);
}
