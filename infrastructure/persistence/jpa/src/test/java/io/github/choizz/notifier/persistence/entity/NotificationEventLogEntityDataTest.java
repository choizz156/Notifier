package io.github.choizz.notifier.persistence.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.persistence.repository.NotificationEventLogJpaRepository;

class NotificationEventLogEntityDataTest extends DataTestContext {


	@Test
	@DisplayName("동일한 notificationId와 retryCount로 이벤트 로그를 저장하면 유니크 제약조건(DataIntegrityViolationException)이 발생한다")
	void test1() {
		// given
		NotificationEventLogEntity entity1 = NotificationEventLogEntity.builder()
			.notificationId(1L)
			.channelType(Channel.IN_APP)
			.eventStatus(EventStatus.RETRIED)
			.failReason("서버 오류")
			.retryCount(0)
			.published(false)
			.build();

		notificationEventLogJpaRepository.saveAndFlush(entity1);

		NotificationEventLogEntity entity2 = NotificationEventLogEntity.builder()
			.notificationId(1L)
			.channelType(Channel.IN_APP)
			.eventStatus(EventStatus.RETRIED)
			.failReason("서버 오류")
			.retryCount(0)
			.published(false)
			.build();

		// when & then
		assertThatThrownBy(() -> notificationEventLogJpaRepository.saveAndFlush(entity2))
			.isInstanceOf(DataIntegrityViolationException.class);
	}
}
