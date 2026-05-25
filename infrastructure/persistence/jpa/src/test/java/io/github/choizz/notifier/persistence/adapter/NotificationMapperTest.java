package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.jpa.entity.NotificationEntity;
import io.github.choizz.notifier.persistence.jpa.adapter.NotificationMapper;

class NotificationMapperTest {

	@DisplayName("Notification 도메인 객체를 Entity로 변환한다.")
	@Test
	void test1() {
		// given
		LocalDateTime now = LocalDateTime.now();
		Notification domain = Notification.builder()
			.id(1L)
			.subscriberId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{\"key\":\"value\"}")
			.status(NotificationStatus.PENDING)
			.failMessage("error")
			.isRead(true)
			.recoverCount(2)
			.createdAt(now.minusDays(1))
			.updatedAt(now)
			.build();

		// when
		NotificationEntity entity = NotificationMapper.toEntity(domain);

		// then
		assertThat(entity.id()).isEqualTo(1L);
		assertThat(entity.subscriberId()).isEqualTo(100L);
		assertThat(entity.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(entity.channel()).isEqualTo(Channel.EMAIL);
		assertThat(entity.metadata()).isEqualTo("{\"key\":\"value\"}");
		assertThat(entity.status()).isEqualTo(NotificationStatus.PENDING);
		assertThat(entity.message()).isEqualTo("error");
		assertThat(entity.isRead()).isTrue();
		assertThat(entity.manualRetryCount()).isEqualTo(2);
		assertThat(entity.updatedAt()).isEqualTo(now);
	}

	@DisplayName("NotificationEntity 객체를 도메인으로 변환한다.")
	@Test
	void test2() {
		// given
		LocalDateTime now = LocalDateTime.now();
		NotificationEntity entity = NotificationEntity.builder()
			.subscriberId(200L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.channel(Channel.IN_APP)
			.metadata("{}")
			.status(NotificationStatus.COMPLETED)
			.message("success")
			.isRead(false)
			.manualRetryCount(0)
			.build();
		entity.id(2L);
		org.springframework.test.util.ReflectionTestUtils.setField(entity, "createdAt", now.minusDays(1));
		entity.updatedAt(now);

		// when
		Notification domain = NotificationMapper.toDomain(entity);

		// then
		assertThat(domain.id()).isEqualTo(2L);
		assertThat(domain.subscriberId()).isEqualTo(200L);
		assertThat(domain.notificationType()).isEqualTo(NotificationType.COUPON_ISSUED);
		assertThat(domain.channel()).isEqualTo(Channel.IN_APP);
		assertThat(domain.metadata()).isEqualTo("{}");
		assertThat(domain.status()).isEqualTo(NotificationStatus.COMPLETED);
		assertThat(domain.failMessage()).isEqualTo("success");
		assertThat(domain.isRead()).isFalse();
		assertThat(domain.recoverCount()).isEqualTo(0);
		assertThat(domain.createdAt()).isEqualTo(now.minusDays(1));
		assertThat(domain.updatedAt()).isEqualTo(now);
	}
}
