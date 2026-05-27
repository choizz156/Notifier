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
import org.springframework.test.util.ReflectionTestUtils;

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
			.idempotencyKey("idempotencyKey")
			.metadata("{\"key\":\"value\"}")
			.status(NotificationStatus.PENDING)
			.failMessage("failMessage")
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
		assertThat(entity.idempotencyKey()).isEqualTo("idempotencyKey");
		assertThat(entity.metadata()).isEqualTo("{\"key\":\"value\"}");
		assertThat(entity.status()).isEqualTo(NotificationStatus.PENDING);
		assertThat(entity.message()).isEqualTo("failMessage");
		assertThat(entity.isRead()).isTrue();
		assertThat(entity.recoverCount()).isEqualTo(2);
		assertThat(entity.updatedAt()).isEqualTo(now);

		Notification mappedNotification = NotificationMapper.toDomain(entity);

		assertThat(mappedNotification.id()).isEqualTo(1L);
		assertThat(mappedNotification.subscriberId()).isEqualTo(100L);
		assertThat(mappedNotification.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(mappedNotification.channel()).isEqualTo(Channel.EMAIL);
		assertThat(mappedNotification.idempotencyKey()).isEqualTo("idempotencyKey");
		assertThat(mappedNotification.metadata()).isEqualTo("{\"key\":\"value\"}");
		assertThat(mappedNotification.status()).isEqualTo(NotificationStatus.PENDING);
		assertThat(mappedNotification.failMessage()).isEqualTo("failMessage");
		assertThat(mappedNotification.isRead()).isTrue();
		assertThat(mappedNotification.recoverCount()).isEqualTo(2);
	}

	@DisplayName("NotificationEntity 객체를 도메인으로 변환한다.")
	@Test
	void test2() {
		// given
		LocalDateTime now = LocalDateTime.now();
		NotificationEntity entity = NotificationEntity.builder()
			.subscriberId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.idempotencyKey("idempotencyKey")
			.metadata("{\"key\":\"value\"}")
			.status(NotificationStatus.PENDING)
			.message("failMessage")
			.isRead(true)
			.recoverCount(0)
			.build();
		entity.id(2L);
		ReflectionTestUtils.setField(entity, "createdAt", now.minusDays(1));
		entity.updatedAt(now);

		// when
		Notification domain = NotificationMapper.toDomain(entity);

		// then
		assertThat(domain.id()).isEqualTo(2L);
		assertThat(domain.subscriberId()).isEqualTo(100L);
		assertThat(domain.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(domain.channel()).isEqualTo(Channel.EMAIL);
		assertThat(domain.metadata()).isEqualTo("{\"key\":\"value\"}");
		assertThat(domain.status()).isEqualTo(NotificationStatus.PENDING);
		assertThat(domain.failMessage()).isEqualTo("failMessage");
		assertThat(domain.isRead()).isTrue();
		assertThat(domain.recoverCount()).isEqualTo(0);
		assertThat(domain.createdAt()).isEqualTo(now.minusDays(1));
		assertThat(domain.updatedAt()).isEqualTo(now);
	}
}
