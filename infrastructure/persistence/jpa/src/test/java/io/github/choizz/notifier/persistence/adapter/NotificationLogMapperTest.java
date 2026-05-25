package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.NotificationLogEntity;

class NotificationLogMapperTest {

	@DisplayName("NotificationLog 도메인 객체를 Entity로 변환한다.")
	@Test
	void test1() {
		// given
		LocalDateTime now = LocalDateTime.now();
		NotificationLog domain = NotificationLog.builder()
			.id(1L)
			.notificationId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channelType(Channel.EMAIL)
			.eventStatus(EventStatus.SENT)
			.failReason("error")
			.retryCount(2)
			.published(true)
			.publishedAt(now.minusHours(1))
			.createdAt(now.minusDays(1))
			.updatedAt(now)
			.build();

		// when
		NotificationLogEntity entity = NotificationLogMapper.toEntity(domain);

		// then
		assertThat(entity.id()).isEqualTo(1L);
		assertThat(entity.notificationId()).isEqualTo(100L);
		assertThat(entity.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(entity.channelType()).isEqualTo(Channel.EMAIL);
		assertThat(entity.eventStatus()).isEqualTo(EventStatus.SENT);
		assertThat(entity.failReason()).isEqualTo("error");
		assertThat(entity.retryCount()).isEqualTo(2);
		assertThat(entity.published()).isTrue();
		assertThat(entity.publishedAt()).isEqualTo(now.minusHours(1));
		assertThat(entity.updatedAt()).isEqualTo(now);
	}

	@DisplayName("NotificationLogEntity 객체를 도메인으로 변환한다.")
	@Test
	void test2() {
		// given
		LocalDateTime now = LocalDateTime.now();
		NotificationLogEntity entity = NotificationLogEntity.builder()
			.notificationId(200L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.channelType(Channel.IN_APP)
			.eventStatus(EventStatus.FAILED)
			.failReason("timeout")
			.retryCount(3)
			.published(false)
			.publishedAt(null)
			.build();
		entity.id(2L);
		org.springframework.test.util.ReflectionTestUtils.setField(entity, "createdAt", now.minusDays(1));
		entity.updatedAt(now);

		// when
		NotificationLog domain = NotificationLogMapper.toDomain(entity);

		// then
		assertThat(domain.id()).isEqualTo(2L);
		assertThat(domain.notificationId()).isEqualTo(200L);
		assertThat(domain.notificationType()).isEqualTo(NotificationType.COUPON_ISSUED);
		assertThat(domain.channelType()).isEqualTo(Channel.IN_APP);
		assertThat(domain.eventStatus()).isEqualTo(EventStatus.FAILED);
		assertThat(domain.failReason()).isEqualTo("timeout");
		assertThat(domain.retryCount()).isEqualTo(3);
		assertThat(domain.published()).isFalse();
		assertThat(domain.publishedAt()).isNull();
		assertThat(domain.createdAt()).isEqualTo(now.minusDays(1));
		assertThat(domain.updatedAt()).isEqualTo(now);
	}
}
