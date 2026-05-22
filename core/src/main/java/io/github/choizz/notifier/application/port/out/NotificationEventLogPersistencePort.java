package io.github.choizz.notifier.application.port.out;

import io.github.choizz.notifier.domain.model.NotificationEventLog;

public interface NotificationEventLogPersistencePort {

	NotificationEventLog save(NotificationEventLog eventLog);
}
