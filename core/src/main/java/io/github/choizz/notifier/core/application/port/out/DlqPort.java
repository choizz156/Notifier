package io.github.choizz.notifier.core.application.port.out;

import java.util.List;

import io.github.choizz.notifier.core.application.dto.DlqRecoveryTarget;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;

public interface DlqPort {
	void saveDlq(PublicNotificationRequestedEvent event, Exception exception);
	
	List<DlqRecoveryTarget> findPendingDlqs(int limit);
	
	void markAsResolved(Long dlqId);
}
