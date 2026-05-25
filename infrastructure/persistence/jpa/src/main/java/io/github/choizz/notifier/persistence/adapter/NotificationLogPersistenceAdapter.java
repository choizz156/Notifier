package io.github.choizz.notifier.persistence.adapter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;
import io.github.choizz.notifier.persistence.repository.NotificationEventLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class NotificationLogPersistenceAdapter implements NotificationLogPersistencePort {

	private final NotificationEventLogJpaRepository eventLogJpaRepository;

	@Override
	@Transactional
	public void save(NotificationLog eventLog) {

		log.info("알림 이벤트 이력 저장 - notificationId={}, eventType={}, eventStatus={}",
			eventLog.notificationId(), eventLog.channelType(), eventLog.eventStatus());

		NotificationEventLogEntity entity = NotificationLogMapper.toEntity(eventLog);
		eventLogJpaRepository.save(entity);
	}

	@Override
	@Transactional
	public void saveAll(List<NotificationLog> eventLogs) {

		List<NotificationEventLogEntity> entities = eventLogs.stream()
			.map(NotificationLogMapper::toEntity)
			.toList();
		eventLogJpaRepository.saveAll(entities);
	}

	@Override
	public NotificationLog findLatestByNotificationId(Long notificationId) {

		return eventLogJpaRepository.findFirstByNotificationIdOrderByCreatedAtDesc(notificationId)
			.map(NotificationLogMapper::toDomain)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 알림 정보 입니다. %s".formatted(notificationId)));
	}

	@Override
	public List<Long> findUnprocessedNotificationIds(List<EventStatus> statuses, long lastId, int chunkSize) {
		return eventLogJpaRepository.findUnprocessedNotificationIds(statuses, lastId, Limit.of(chunkSize));
	}

	@Override
	public List<NotificationLog> findAllByEventStatus(EventStatus eventStatus) {
		return eventLogJpaRepository.findAllByEventStatus(eventStatus)
			.stream()
			.map(NotificationLogMapper::toDomain)
			.toList();
	}

	@Override
	public List<NotificationLog> findStuckLogs(long lastId, EventStatus status, Collection<NotificationType> types, LocalDateTime thresholdTime, int chunkSize) {
		return eventLogJpaRepository.findStuckLogs(lastId, status, types, thresholdTime, Limit.of(chunkSize))
			.stream()
			.map(NotificationLogMapper::toDomain)
			.toList();
	}
	}

