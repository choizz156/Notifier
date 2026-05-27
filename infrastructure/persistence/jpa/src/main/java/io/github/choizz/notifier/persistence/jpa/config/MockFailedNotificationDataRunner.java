package io.github.choizz.notifier.persistence.jpa.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationEntity;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationLogEntity;
import io.github.choizz.notifier.persistence.jpa.repository.NotificationJpaRepository;
import io.github.choizz.notifier.persistence.jpa.repository.NotificationLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Component
public class MockFailedNotificationDataRunner implements ApplicationRunner {

	private final NotificationJpaRepository notificationJpaRepository;
	private final NotificationLogJpaRepository notificationLogJpaRepository;

	@Transactional
	@Override
	public void run(ApplicationArguments args) {
		if (notificationJpaRepository.existsBySubscriberIdAndNotificationTypeAndChannelAndStatus(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, NotificationStatus.FAILED)) {
			log.info("Mock 실패 알림 데이터가 이미 존재할 수 있습니다. 초기화를 건너뜁니다.");
			return;
		}

		log.info("Mock 실패 알림 데이터 초기화를 시작합니다 (재시도 테스트용)...");

		// 1. 실패 상태의 NotificationEntity 생성
		NotificationEntity failedNotification = NotificationEntity.builder()
			.subscriberId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.IN_APP)
			.idempotencyKey("mock-failed-uuid-1234")
			.metadata("{\"orderId\":\"failed-order-1\"}")
			.status(NotificationStatus.FAILED)
			.message("Mock 발송 실패 사유")
			.isRead(false)
			.recoverCount(0)
			.build();

		NotificationEntity savedNotification = notificationJpaRepository.save(failedNotification);

		// 2. 실패 상태의 NotificationLogEntity 생성
		NotificationLogEntity failedLog = NotificationLogEntity.builder()
			.referenceId(savedNotification.id())
			.referenceType(ReferenceType.PERSONAL)
			.subscriberId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channelType(Channel.IN_APP)
			.eventStatus(EventStatus.FAILED)
			.failReason("Mock 실패: 타임아웃 발생")
			.retryCount(0)
			.published(false)
			.metadata("{\"orderId\":\"failed-order-1\"}")
			.build();

		notificationLogJpaRepository.save(failedLog);

		log.info("Mock 실패 알림(ID: {}) 및 로그 초기화 완료! API 7번(/v1/notifications/retry)으로 재시도를 테스트해보세요.", savedNotification.id());
	}
}
