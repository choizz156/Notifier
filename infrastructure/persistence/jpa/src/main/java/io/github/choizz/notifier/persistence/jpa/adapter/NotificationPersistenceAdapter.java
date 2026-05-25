package io.github.choizz.notifier.persistence.jpa.adapter;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationEntity;
import io.github.choizz.notifier.core.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.persistence.jpa.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class NotificationPersistenceAdapter implements NotificationPersistencePort {

	private final NotificationJpaRepository notificationJpaRepository;

	@Transactional
	@Override
	public Notification save(Notification notification) {
		NotificationEntity entity = NotificationMapper.toEntity(notification);
		NotificationEntity NotificationEntity = notificationJpaRepository.save(entity);

		return NotificationMapper.toDomain(NotificationEntity);
	}

	@Transactional
	@Override
	public void markAsRead(Long id) {
		notificationJpaRepository.markAsRead(id);
	}

	@Override
	public Notification findById(Long id) {
		NotificationEntity entity = notificationJpaRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("알람을 찾을 수 없습니다. id=" + id)
			);

		return NotificationMapper.toDomain(entity);
	}

	@Override
	public Notification findBySubscriberId(Long subscriberId) {
		NotificationEntity entity = notificationJpaRepository.findBySubscriberId(subscriberId)
			.orElseThrow(() ->
				new NoSuchElementException("구독자의 알람을 찾을 수 없습니다. subscriberId=" + subscriberId)
			);

		return NotificationMapper.toDomain(entity);
	}

	@Override
	public boolean existsDuplicate(Long subscriberId, NotificationType notificationType, Channel channel) {
		return notificationJpaRepository.existsBySubscriberIdAndNotificationTypeAndChannelAndStatus(
			subscriberId,
			notificationType,
			channel,
			NotificationStatus.PENDING
		);
	}

	@Override
	public PageResult<Notification> findAllBySubscriberId(Long subscriberId, Boolean isRead, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<NotificationEntity> entityPage;
		
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
		return notificationJpaRepository.findAllById(ids).stream()
			.map(NotificationMapper::toDomain)
			.toList();
	}

	@Override
	@Transactional
	public List<Notification> saveAll(List<Notification> notifications) {
		List<NotificationEntity> entities = notifications.stream()
			.map(NotificationMapper::toEntity)
			.toList();
		List<NotificationEntity> savedEntities = notificationJpaRepository.saveAll(entities);
		return savedEntities.stream()
			.map(NotificationMapper::toDomain)
			.toList();
	}
}

