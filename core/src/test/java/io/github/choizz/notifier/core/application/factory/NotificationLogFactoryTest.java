package io.github.choizz.notifier.core.application.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;

class NotificationLogFactoryTest {

	private final NotificationLogFactory factory = new NotificationLogFactory();

	@DisplayName("EventStatus가 SENT일 때 알림 로그를 생성한다.")
	@Test
	void test1() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("success")
			.metadata("{}")
			.retryCount(0)
			.build();

		// when
		NotificationLog log = factory.create(EventStatus.SENT, context);

		// then
		assertThat(log.notificationId()).isEqualTo(1L);
		assertThat(log.eventStatus()).isEqualTo(EventStatus.SENT);
		assertThat(log.retryCount()).isEqualTo(0);
	}

	@DisplayName("EventStatus가 RETRIED일 때 재시도 알림 로그를 생성한다.")
	@Test
	void test2() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("timeout")
			.metadata("{}")
			.retryCount(1)
			.build();

		// when
		NotificationLog log = factory.create(EventStatus.RETRIED, context);

		// then
		assertThat(log.notificationId()).isEqualTo(1L);
		assertThat(log.eventStatus()).isEqualTo(EventStatus.RETRIED);
		assertThat(log.failReason()).isEqualTo("timeout");
		assertThat(log.retryCount()).isEqualTo(1);
	}

	@DisplayName("EventStatus가 FAILED일 때 실패 알림 로그를 생성한다.")
	@Test
	void test3() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.failReason("fatal error")
			.metadata("{}")
			.retryCount(3)
			.build();

		// when
		NotificationLog log = factory.create(EventStatus.FAILED, context);

		// then
		assertThat(log.notificationId()).isEqualTo(1L);
		assertThat(log.eventStatus()).isEqualTo(EventStatus.FAILED);
		assertThat(log.failReason()).isEqualTo("fatal error");
		assertThat(log.retryCount()).isEqualTo(3);
	}

	@DisplayName("EventStatus가 REQUESTED 또는 PROCESSING일 때 생성하려 하면 예외가 발생한다.")
	@Test
	void test4() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.metadata("{}")
			.retryCount(0)
			.build();

		// when & then
		assertThatThrownBy(() -> factory.create(EventStatus.REQUESTED, context))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("변경할 이벤트 로그는 REQUESTED 또는 PROCESSING 상태가 아니어야합니다.");

		assertThatThrownBy(() -> factory.create(EventStatus.PROCESSING, context))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("변경할 이벤트 로그는 REQUESTED 또는 PROCESSING 상태가 아니어야합니다.");
	}
}
