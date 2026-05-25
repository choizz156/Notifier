package io.github.choizz.notifier.core.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationLogTest {

	@DisplayName("알림 로그를 REQUESTED 상태로 생성한다.")
	@Test
	void test1() {
		// when
		NotificationLog log = NotificationLog.request(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "{}");

		// then
		assertThat(log.notificationId()).isEqualTo(1L);
		assertThat(log.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(log.channelType()).isEqualTo(Channel.EMAIL);
		assertThat(log.eventStatus()).isEqualTo(EventStatus.REQUESTED);
		assertThat(log.published()).isFalse();
		assertThat(log.retryCount()).isEqualTo(0);
	}

	@DisplayName("알림 로그를 RETRIED 상태로 생성한다.")
	@Test
	void test2() {
		// when
		NotificationLog log = NotificationLog.retried(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "timeout", "{}", 1);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.RETRIED);
		assertThat(log.failReason()).isEqualTo("timeout");
		assertThat(log.retryCount()).isEqualTo(1);
		assertThat(log.published()).isFalse();
	}

	@DisplayName("알림 로그를 SENT 상태로 생성한다.")
	@Test
	void test3() {
		// when
		NotificationLog log = NotificationLog.sent(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "{}", 0);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.SENT);
		assertThat(log.published()).isTrue();
		assertThat(log.publishedAt()).isNotNull();
	}

	@DisplayName("알림 로그를 FAILED 상태로 생성한다.")
	@Test
	void test4() {
		// when
		NotificationLog log = NotificationLog.failed(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "error", "{}", 3);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.FAILED);
		assertThat(log.failReason()).isEqualTo("error");
		assertThat(log.retryCount()).isEqualTo(3);
		assertThat(log.published()).isFalse();
	}

	@DisplayName("알림 로그를 PROCESSING 상태로 변경한다.")
	@Test
	void test5() {
		// given
		NotificationLog log = NotificationLog.request(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "{}");

		// when
		log.markAsProcessing();

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.PROCESSING);
	}

	@DisplayName("알림 로그를 RETRIED 상태로 변경한다.")
	@Test
	void test6() {
		// given
		NotificationLog log = NotificationLog.request(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "{}");

		// when
		log.markAsRetried("timeout", 1);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.RETRIED);
		assertThat(log.failReason()).isEqualTo("timeout");
		assertThat(log.retryCount()).isEqualTo(1);
	}

	@DisplayName("알림 로그를 FAILED 상태로 변경한다.")
	@Test
	void test7() {
		// given
		NotificationLog log = NotificationLog.request(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "{}");

		// when
		log.markAsFailed("error", 3);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.FAILED);
		assertThat(log.failReason()).isEqualTo("error");
		assertThat(log.retryCount()).isEqualTo(3);
	}
}
