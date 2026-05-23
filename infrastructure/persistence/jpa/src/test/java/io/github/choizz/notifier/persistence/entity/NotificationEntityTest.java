package io.github.choizz.notifier.persistence.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;

class NotificationEntityTest extends DataTestContext {

	@Test
	@DisplayName("동일한 subscriberId, notificationType, channel로 알림을 저장하면 유니크 제약조건(DataIntegrityViolationException)이 발생한다")
	void test1() {
		// given
		NotificationEntity entity1 = NotificationEntity.builder()
			.subscriberId(1L)
			.notificationType(NotificationType.ENROLLMENT_COMPLETED)
			.channel(Channel.IN_APP)
			.status(NotificationStatus.PENDING)
			.message("첫 번째 알림")
			.isRead(false)
			.build();

		notificationJpaRepository.saveAndFlush(entity1);

		NotificationEntity entity2 = NotificationEntity.builder()
			.subscriberId(1L)
			.notificationType(NotificationType.ENROLLMENT_COMPLETED)
			.channel(Channel.IN_APP)
			.status(NotificationStatus.PENDING)
			.message("두 번째 알림")
			.isRead(false)
			.build();

		// when & then
		assertThatThrownBy(() -> notificationJpaRepository.saveAndFlush(entity2))
			.isInstanceOf(DataIntegrityViolationException.class);
	}
}