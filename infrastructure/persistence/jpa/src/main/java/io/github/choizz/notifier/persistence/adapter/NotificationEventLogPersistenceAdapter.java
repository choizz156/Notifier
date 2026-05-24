package io.github.choizz.notifier.persistence.adapter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.out.NotificationEventLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationEventLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
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
	@Transactional
	public void saveAll(List<NotificationEventLog> eventLogs) {

		List<NotificationEventLogEntity> entities = eventLogs.stream()
			.map(NotificationEventLogMapper::toEntity)
			.toList();
		eventLogJpaRepository.saveAll(entities);
	}

	@Override
	public NotificationEventLog findLatestByNotificationId(Long notificationId) {

		return eventLogJpaRepository.findFirstByNotificationIdOrderByCreatedAtDesc(notificationId)
			.map(NotificationEventLogMapper::toDomain)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 알림 정보 입니다. %s".formatted(notificationId)));
	}

	@Override
	public List<Long> findUnprocessedNotificationIds(List<EventStatus> statuses, long lastId, int chunkSize) {
		return eventLogJpaRepository.findUnprocessedNotificationIds(statuses, lastId, Limit.of(chunkSize));
	}

	@Override
	public List<NotificationEventLog> findAllByEventStatus(EventStatus eventStatus) {
		return eventLogJpaRepository.findAllByEventStatus(eventStatus)
			.stream()
			.map(NotificationEventLogMapper::toDomain)
			.toList();
	}

	@Override
	public List<NotificationEventLog> findStuckLogs(long lastId, EventStatus status, Collection<NotificationType> types, LocalDateTime thresholdTime, int chunkSize) {
		return eventLogJpaRepository.findStuckLogs(lastId, status, types, thresholdTime, Limit.of(chunkSize))
			.stream()
			.map(NotificationEventLogMapper::toDomain)
			.toList();
	}
	}

