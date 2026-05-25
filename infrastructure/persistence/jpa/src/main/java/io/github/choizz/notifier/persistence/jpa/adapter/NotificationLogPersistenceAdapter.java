package io.github.choizz.notifier.persistence.jpa.adapter;

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
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationLogEntity;
import io.github.choizz.notifier.persistence.jpa.repository.NotificationLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class NotificationLogPersistenceAdapter implements NotificationLogPersistencePort {

	private final NotificationLogJpaRepository notificationLogJpaRepository;

	@Override
	@Transactional
	public void save(NotificationLog notificationLog) {

		log.info("알림 이벤트 이력 저장 - referenceId={}, referenceType={}, channel={}, eventStatus={}",
			notificationLog.referenceId(), notificationLog.referenceType(), notificationLog.channelType(), notificationLog.eventStatus());

		NotificationLogEntity entity = NotificationLogMapper.toEntity(notificationLog);
		notificationLogJpaRepository.save(entity);
	}

	@Override
	@Transactional
	public void saveAll(List<NotificationLog> notificationLogs) {

		List<NotificationLogEntity> entities = notificationLogs.stream()
			.map(NotificationLogMapper::toEntity)
			.toList();
		notificationLogJpaRepository.saveAll(entities);
	}

	@Override
	public NotificationLog findLatestByReference(Long referenceId, ReferenceType referenceType) {

		return notificationLogJpaRepository.findFirstByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(referenceId, referenceType)
			.map(NotificationLogMapper::toDomain)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 알림 정보 입니다. id:%s, type:%s".formatted(referenceId, referenceType)));
	}

	@Override
	public List<Long> findUnprocessedNotificationIds(List<EventStatus> statuses, long lastId, int chunkSize) {
		return notificationLogJpaRepository.findUnprocessedNotificationIds(statuses, lastId, Limit.of(chunkSize));
	}

	@Override
	public List<NotificationLog> findAllByEventStatus(EventStatus eventStatus) {
		return notificationLogJpaRepository.findAllByEventStatus(eventStatus)
			.stream()
			.map(NotificationLogMapper::toDomain)
			.toList();
	}

	@Override
	public List<NotificationLog> findStuckLogs(long lastId, EventStatus status, Collection<NotificationType> types, LocalDateTime thresholdTime, int chunkSize) {
		return notificationLogJpaRepository.findStuckLogs(lastId, status, types, thresholdTime, Limit.of(chunkSize))
			.stream()
			.map(NotificationLogMapper::toDomain)
			.toList();
	}
	}

