package io.github.choizz.notifier.core.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.application.dto.NotificationContext;

class NotificationTest {

	@DisplayName("NotificationContext와 Channel로부터 Notification을 생성한다.")
	@Test
	void test1() {
		// given
		NotificationContext context = new NotificationContext(1L, "PAYMENT_CONFIRMED", Map.of("key", "value"));

		// when
		Notification notification = Notification.from(context, Channel.EMAIL);

		// then
		assertThat(notification.subscriberId()).isEqualTo(1L);
		assertThat(notification.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(notification.channel()).isEqualTo(Channel.EMAIL);
		assertThat(notification.status()).isEqualTo(NotificationStatus.PENDING);
		assertThat(notification.isRead()).isFalse();
		assertThat(notification.recoverCount()).isEqualTo(0);
	}

	@DisplayName("알림을 성공 상태로 변경한다.")
	@Test
	void test2() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.PENDING)
			.build();

		// when
		notification.markAsCompleted();

		// then
		assertThat(notification.status()).isEqualTo(NotificationStatus.COMPLETED);
	}

	@DisplayName("실패한 알림을 완료 상태로 변경하려 하면 예외가 발생한다.")
	@Test
	void test3() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.FAILED)
			.build();

		// when & then
		assertThatThrownBy(notification::markAsCompleted)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("실패한 알림은 완료할 수 없습니다.");
	}

	@DisplayName("알림을 실패 상태로 변경하고 실패 사유를 기록한다.")
	@Test
	void test4() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.PENDING)
			.build();

		// when
		notification.markAsFailed("network error");

		// then
		assertThat(notification.status()).isEqualTo(NotificationStatus.FAILED);
		assertThat(notification.failMessage()).isEqualTo("network error");
	}

	@DisplayName("이미 실패한 알림을 다시 실패 상태로 변경하려 하면 예외가 발생한다.")
	@Test
	void test5() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.FAILED)
			.build();

		// when & then
		assertThatThrownBy(() -> notification.markAsFailed("another error"))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("이미 실패한 알림입니다.");
	}

	@DisplayName("알림을 재시도 상태로 변경한다.")
	@Test
	void test6() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.PENDING)
			.build();

		// when
		notification.markAsRetrying();

		// then
		assertThat(notification.status()).isEqualTo(NotificationStatus.RETRYING);
	}

	@DisplayName("성공이나 실패 상태인 알림을 재시도 상태로 변경하려 하면 예외가 발생한다.")
	@Test
	void test7() {
		// given
		Notification completed = Notification.builder().status(NotificationStatus.COMPLETED).build();
		Notification failed = Notification.builder().status(NotificationStatus.FAILED).build();

		// when & then
		assertThatThrownBy(completed::markAsRetrying)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("재시도는 준비 중 이거나 재시도 중인 알림에 대해서만 가능합니다.");

		assertThatThrownBy(failed::markAsRetrying)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("재시도는 준비 중 이거나 재시도 중인 알림에 대해서만 가능합니다.");
	}

	@DisplayName("알림을 복구 대기 상태로 변경하면 상태가 PENDING이 되고 복구 횟수가 증가한다.")
	@Test
	void test8() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.FAILED)
			.failMessage("error")
			.recoverCount(1)
			.build();

		// when
		notification.markAsPendingForRecover();

		// then
		assertThat(notification.status()).isEqualTo(NotificationStatus.PENDING);
		assertThat(notification.failMessage()).isNull();
		assertThat(notification.recoverCount()).isEqualTo(2);
	}

	@DisplayName("완료된 알림을 복구 대기 상태로 변경하려 하면 예외가 발생한다.")
	@Test
	void test9() {
		// given
		Notification notification = Notification.builder()
			.status(NotificationStatus.COMPLETED)
			.build();

		// when & then
		assertThatThrownBy(notification::markAsPendingForRecover)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("완료된 알림은 재시도할 수 없습니다.");
	}
}
