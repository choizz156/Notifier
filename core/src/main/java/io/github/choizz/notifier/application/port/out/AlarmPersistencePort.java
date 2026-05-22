package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.model.Alarm;
import io.github.choizz.notifier.domain.model.AlarmStatus;


public interface AlarmPersistencePort {

	Alarm save(Alarm alarm);

	void updateStatus(long id, AlarmStatus alarmStatus);

	Alarm findById(Long id);

	Alarm findBySubscriberId(Long subscriberId);
}
