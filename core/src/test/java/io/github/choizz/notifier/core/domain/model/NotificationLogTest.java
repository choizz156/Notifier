package io.github.choizz.notifier.core.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.Notification;

class NotificationLogTest {

	@DisplayName("알림 로그를 REQUESTED 상태로 생성한다.")
	@Test
	void test1() {
		// when
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog log = NotificationLog.request(notification);

		// then
		assertThat(log.referenceId()).isEqualTo(1L);
		assertThat(log.referenceType()).isEqualTo(ReferenceType.PERSONAL);
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
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.referenceType(ReferenceType.PERSONAL.name())
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("timeout")
			.metadata("{}")
			.retryCount(1)
			.build();
		NotificationLog log = NotificationLog.retried(context);

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
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.referenceType(ReferenceType.PERSONAL.name())
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.metadata("{}")
			.retryCount(0)
			.build();
		NotificationLog log = NotificationLog.sent(context);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.SENT);
		assertThat(log.published()).isTrue();
		assertThat(log.publishedAt()).isNotNull();
	}

	@DisplayName("알림 로그를 FAILED 상태로 생성한다.")
	@Test
	void test4() {
		// when
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.referenceType(ReferenceType.PERSONAL.name())
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("error")
			.metadata("{}")
			.retryCount(3)
			.build();
		NotificationLog log = NotificationLog.failed(context);

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
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog log = NotificationLog.request(notification);

		// when
		log.markAsProcessing();

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.PROCESSING);
	}

	@DisplayName("알림 로그를 RETRIED 상태로 변경한다.")
	@Test
	void test6() {
		// given
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog log = NotificationLog.request(notification);

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
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog log = NotificationLog.request(notification);

		// when
		log.markAsFailed("error", 3);

		// then
		assertThat(log.eventStatus()).isEqualTo(EventStatus.FAILED);
		assertThat(log.failReason()).isEqualTo("error");
		assertThat(log.retryCount()).isEqualTo(3);
	}
}
