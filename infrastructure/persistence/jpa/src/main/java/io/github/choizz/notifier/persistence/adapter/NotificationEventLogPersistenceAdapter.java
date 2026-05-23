package io.github.choizz.notifier.persistence.adapter;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;
import io.github.choizz.notifier.persistence.repository.NotificationEventLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class NotificationEventLogPersistenceAdapter implements NotificationEventLogPersistencePort {

	private final NotificationEventLogJpaRepository eventLogJpaRepository;

	@Override
	@Transactional
	public void save(NotificationEventLog eventLog) {

		log.info("알림 이벤트 이력 저장 - notificationId={}, eventType={}, eventStatus={}",
			eventLog.notificationId(), eventLog.channelType(), eventLog.eventStatus());

		NotificationEventLogEntity entity = NotificationEventLogMapper.toEntity(eventLog);
		eventLogJpaRepository.save(entity);
	}

	@Override
	public NotificationEventLog findLatestByNotificationId(Long notificationId) {

		return eventLogJpaRepository.findFirstByNotificationIdOrderByCreatedAtDesc(notificationId)
			.map(NotificationEventLogMapper::toDomain)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 알림 정보 입니다. %s".formatted(notificationId)));
	}

	@Override
	public java.util.List<Long> findUnprocessedNotificationIds(java.util.List<io.github.choizz.notifier.core.domain.model.EventStatus> statuses) {
		return eventLogJpaRepository.findUnprocessedNotificationIds(statuses);
	}
}
