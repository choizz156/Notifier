package io.github.choizz.notifier.core.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationInformationTest {

	@DisplayName("올바른 파라미터로 공개 예약 정보를 생성한다.")
	@Test
	void test1() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when
		ReservationInformation reservation = ReservationInformation.ofPublic(NotificationType.NEW_LECTURE_OPENED, "{\"key\":\"value\"}", futureTime);

		// then
		assertThat(reservation.notificationType()).isEqualTo(NotificationType.NEW_LECTURE_OPENED);
		assertThat(reservation.metadata()).isEqualTo("{\"key\":\"value\"}");
		assertThat(reservation.reservationTime()).isEqualTo(futureTime);
		assertThat(reservation.isPublished()).isFalse();
	}

	@DisplayName("예약 발송을 지원하지 않는 알림 타입으로 예약하려 하면 예외가 발생한다.")
	@Test
	void test2() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when & then
		assertThatThrownBy(() -> ReservationInformation.ofPublic(NotificationType.PAYMENT_CONFIRMED, null, futureTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("해당 알림 타입은 예약 발송을 지원하지 않습니다");
	}

	@DisplayName("예약 시간이 정각(1시간 단위)이 아니면 예외가 발생한다.")
	@Test
	void test3() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0);

		// when & then
		assertThatThrownBy(() -> ReservationInformation.ofPublic(NotificationType.COUPON_ISSUED, null, futureTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("예약 시간은 1시간 단위(정각)로만 설정 가능합니다");
	}

	@DisplayName("과거 시간으로 예약하려 하면 예외가 발생한다.")
	@Test
	void test4() {
		// given
		LocalDateTime pastTime = LocalDateTime.now().minusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when & then
		assertThatThrownBy(() -> ReservationInformation.ofPublic(NotificationType.COUPON_ISSUED, null, pastTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("과거 시간으로 예약할 수 없습니다");
	}

	@DisplayName("예약 정보를 발행 완료 상태로 변경한다.")
	@Test
	void test5() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
		ReservationInformation reservation = ReservationInformation.ofPublic(NotificationType.COUPON_ISSUED, null, futureTime);

		// when
		reservation.markAsPublished();

		// then
		assertThat(reservation.isPublished()).isTrue();
	}

	@DisplayName("메타데이터가 null이거나 빈 값인 경우에도 예약 정보가 정상 생성된다.")
	@Test
	void test6() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when & then (null 케이스)
		ReservationInformation reservationNull = ReservationInformation.ofPublic(NotificationType.COUPON_ISSUED, null, futureTime);
		assertThat(reservationNull.metadata()).isNull();

		// when & then (빈 값 케이스)
		ReservationInformation reservationEmpty = ReservationInformation.ofPublic(NotificationType.COUPON_ISSUED, "", futureTime);
		assertThat(reservationEmpty.metadata()).isEmpty();
	}
}

