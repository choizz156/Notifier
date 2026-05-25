package io.github.choizz.notifier.persistence.jpa.adapter;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationEntity;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.persistence.jpa.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class NotificationPersistenceAdapter implements NotificationPersistencePort {

	private final NotificationJpaRepository notificationJpaRepository;

	@Transactional
	@Override
	public Notification save(Notification notification) {
		log.info("알림 저장 실행 - 알림: {}", notification);

		NotificationEntity entity = NotificationMapper.toEntity(notification);
		NotificationEntity NotificationEntity = notificationJpaRepository.save(entity);

		return NotificationMapper.toDomain(NotificationEntity);
	}

	@Transactional
	@Override
	public void markAsRead(Long id) {
		log.info("알림 읽음 처리 실행 - 알림 ID: {}", id);
		int updatedCount = notificationJpaRepository.markAsRead(id);
		if (updatedCount == 0) {
			log.info("알림(ID: {})은 이미 읽음 처리되었거나 존재하지 않습니다.", id);
		}
	}

	@Override
	public Notification findById(Long id) {
		log.info("알림 단건 조회 실행 - 알림 ID: {}", id);

		NotificationEntity entity = notificationJpaRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("알람을 찾을 수 없습니다. id=" + id)
			);

		return NotificationMapper.toDomain(entity);
	}

	@Override
	public Notification findBySubscriberId(Long subscriberId) {
		log.info("구독자 알림 단건 조회 실행 - 구독자 ID: {}", subscriberId);

		NotificationEntity entity = notificationJpaRepository.findBySubscriberId(subscriberId)
			.orElseThrow(() ->
				new NoSuchElementException("구독자의 알람을 찾을 수 없습니다. subscriberId=" + subscriberId)
			);

		return NotificationMapper.toDomain(entity);
	}

	@Override
	public boolean existsDuplicate(Long subscriberId, NotificationType notificationType, Channel channel) {
		log.info("중복 알림 존재 여부 확인 실행 - 구독자 ID: {}, 알림 타입: {}, 채널: {}", subscriberId, notificationType, channel);

		return notificationJpaRepository.existsBySubscriberIdAndNotificationTypeAndChannelAndStatus(
			subscriberId,
			notificationType,
			channel,
			NotificationStatus.PENDING
		);
	}

	@Override
	public PageResult<Notification> findAllBySubscriberId(Long subscriberId, Boolean isRead, int page, int size) {
		log.info("구독자 알림 목록 페이징 조회 실행 - 구독자 ID: {}, 읽음 여부: {}, 페이지: {}, 사이즈: {}", subscriberId, isRead, page, size);
		org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
		org.springframework.data.domain.Page<NotificationEntity> entityPage;
		
		if (isRead == null) {
			entityPage = notificationJpaRepository.findBySubscriberId(subscriberId, pageable);
		} else {
			entityPage = notificationJpaRepository.findBySubscriberIdAndIsRead(subscriberId, isRead, pageable);
		}

		List<Notification> notifications = entityPage.getContent().stream()
			.map(NotificationMapper::toDomain)
			.toList();

		return new PageResult<>(
			notifications,
			entityPage.getNumber(),
			entityPage.getSize(),
			entityPage.getTotalElements(),
			entityPage.getTotalPages()
		);
	}

	@Override
	public List<Notification> findAllByIds(List<Long> ids) {
		log.info("알림 다건 조회 실행 - 알림 IDs: {}", ids);
		return notificationJpaRepository.findAllById(ids).stream()
			.map(NotificationMapper::toDomain)
			.toList();
	}

	@Override
	@Transactional
	public List<Notification> saveAll(List<Notification> notifications) {
		log.info("알림 다건 저장 실행 - 알림 개수: {}", notifications.size());
		List<NotificationEntity> entities = notifications.stream()
			.map(NotificationMapper::toEntity)
			.toList();
		List<NotificationEntity> savedEntities = notificationJpaRepository.saveAll(entities);
		return savedEntities.stream()
			.map(NotificationMapper::toDomain)
			.toList();
	}
}

