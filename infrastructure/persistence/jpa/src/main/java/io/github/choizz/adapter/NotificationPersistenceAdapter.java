package io.github.choizz.adapter;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;

import io.github.choizz.entity.NotificationEntity;
import io.github.choizz.notifier.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.domain.model.Notification;
import io.github.choizz.notifier.domain.model.NotificationStatus;
import io.github.choizz.notifier.domain.model.NotificationType;
import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.repository.NotificationJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Repository
public class NotificationPersistenceAdapter implements NotificationPersistencePort {

	private static final List<NotificationStatus> ACTIVE_STATUSES = List.of(
		NotificationStatus.PENDING,
		NotificationStatus.SENDING
	);

	private final NotificationJpaRepository notificationJpaRepository;

	@Override
	public Notification save(Notification notification) {

		NotificationEntity entity = NotificationMapper.toEntity(notification);
		NotificationEntity NotificationEntity = notificationJpaRepository.save(entity);

		return NotificationMapper.toDomain(NotificationEntity);
	}

	@Override
	public void updateStatus(long id, NotificationStatus notificationStatus) {

		NotificationEntity entity = notificationJpaRepository.findById(id).orElseThrow();
		Notification notification = NotificationMapper.toDomain(entity);

		switch (notificationStatus) {
			case COMPLETED -> notification.markAsCompleted();
			case FAILED -> notification.markAsFailed();
			case RETRYING -> notification.markAsRetrying();
			case SENDING -> notification.markAsSending();
		}

		NotificationEntity updatedEntity = NotificationMapper.toEntity(notification);
		updatedEntity.id(entity.id());
		notificationJpaRepository.save(updatedEntity);
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

		return notificationJpaRepository.existsBySubscriberIdAndNotificationTypeAndChannelAndStatusIn(
			subscriberId,
			notificationType,
			channel,
			ACTIVE_STATUSES
		);
	}
}

