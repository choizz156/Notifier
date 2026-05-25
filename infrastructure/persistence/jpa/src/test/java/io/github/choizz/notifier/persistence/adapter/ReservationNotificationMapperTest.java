package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import io.github.choizz.notifier.persistence.jpa.entity.ReservationNotificationEntity;
import io.github.choizz.notifier.persistence.jpa.adapter.ReservationNotificationMapper;

class ReservationNotificationMapperTest {

	@DisplayName("ReservationInformation 도메인 객체를 Entity로 변환한다.")
	@Test
	void test1() {
		// given
		LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);
		ReservationInformation domain = ReservationInformation.builder()
			.id(1L)
			.subscriberId(100L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.reservationTime(reservationTime)
			.isPublished(true)
			.build();

		// when
		ReservationNotificationEntity entity = ReservationNotificationMapper.toEntity(domain);

		// then
		assertThat(entity.id()).isEqualTo(1L);
		assertThat(entity.subscriberId()).isEqualTo(100L);
		assertThat(entity.notificationType()).isEqualTo(NotificationType.COUPON_ISSUED);
		assertThat(entity.reservationTime()).isEqualTo(reservationTime);
		assertThat(entity.isPublished()).isTrue();
	}

	@DisplayName("ReservationNotificationEntity 객체를 도메인으로 변환한다.")
	@Test
	void test2() {
		// given
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime reservationTime = now.plusDays(1);
		ReservationNotificationEntity entity = ReservationNotificationEntity.builder()
			.subscriberId(200L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.reservationTime(reservationTime)
			.isPublished(false)
			.build();
		entity.id(2L);
		org.springframework.test.util.ReflectionTestUtils.setField(entity, "createdAt", now.minusDays(1));
		entity.updatedAt(now);

		// when
		ReservationInformation domain = ReservationNotificationMapper.toDomain(entity);

		// then
		assertThat(domain.id()).isEqualTo(2L);
		assertThat(domain.subscriberId()).isEqualTo(200L);
		assertThat(domain.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(domain.reservationTime()).isEqualTo(reservationTime);
		assertThat(domain.isPublished()).isFalse();
		assertThat(domain.createdAt()).isEqualTo(now.minusDays(1));
		assertThat(domain.updatedAt()).isEqualTo(now);
	}
}
