package io.github.choizz.notifier.core.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationInformationTest {

	@DisplayName("올바른 파라미터로 예약 정보를 생성한다.")
	@Test
	void test1() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when
		ReservationInformation reservation = ReservationInformation.of(1L, NotificationType.COUPON_ISSUED, futureTime);

		// then
		assertThat(reservation.subscriberId()).isEqualTo(1L);
		assertThat(reservation.notificationType()).isEqualTo(NotificationType.COUPON_ISSUED);
		assertThat(reservation.reservationTime()).isEqualTo(futureTime);
		assertThat(reservation.isPublished()).isFalse();
	}

	@DisplayName("예약 발송을 지원하지 않는 알림 타입으로 예약하려 하면 예외가 발생한다.")
	@Test
	void test2() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when & then
		assertThatThrownBy(() -> ReservationInformation.of(1L, NotificationType.PAYMENT_CONFIRMED, futureTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("해당 알림 타입은 예약 발송을 지원하지 않습니다");
	}

	@DisplayName("예약 시간이 정각(1시간 단위)이 아니면 예외가 발생한다.")
	@Test
	void test3() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0);

		// when & then
		assertThatThrownBy(() -> ReservationInformation.of(1L, NotificationType.COUPON_ISSUED, futureTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("예약 시간은 1시간 단위(정각)로만 설정 가능합니다");
	}

	@DisplayName("과거 시간으로 예약하려 하면 예외가 발생한다.")
	@Test
	void test4() {
		// given
		LocalDateTime pastTime = LocalDateTime.now().minusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when & then
		assertThatThrownBy(() -> ReservationInformation.of(1L, NotificationType.COUPON_ISSUED, pastTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("과거 시간으로 예약할 수 없습니다");
	}

	@DisplayName("예약 정보를 발행 완료 상태로 변경한다.")
	@Test
	void test5() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
		ReservationInformation reservation = ReservationInformation.of(1L, NotificationType.COUPON_ISSUED, futureTime);

		// when
		reservation.markAsPublished();

		// then
		assertThat(reservation.isPublished()).isTrue();
	}
}
