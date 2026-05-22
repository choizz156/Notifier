package io.github.choizz.notifier.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.application.dto.NotificationContext;
import io.github.choizz.notifier.application.dto.NotificationResponse;
import io.github.choizz.notifier.application.dto.NotificationStatusResponse;
import io.github.choizz.notifier.application.dto.PageResult;
import io.github.choizz.notifier.application.port.out.NotificationPersistencePort;
import io.github.choizz.notifier.domain.model.Channel;
import io.github.choizz.notifier.domain.model.Notification;
import io.github.choizz.notifier.domain.model.NotificationStatus;
import io.github.choizz.notifier.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

	@Mock
	private NotificationPersistencePort persistencePort;

	@InjectMocks
	private NotificationQueryService sut;

	@Test
	@DisplayName("특정 알림의 상태를 조회할 수 있다.")
	void test1() {
		// given
		Notification notification = Notification.from(new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of()
		));
		given(persistencePort.findById(10L)).willReturn(notification);

		// when
		NotificationStatusResponse response = sut.getStatus(10L);

		// then
		assertThat(response.status()).isEqualTo(NotificationStatus.PENDING);
	}

	@Test
	@DisplayName("사용자 식별자와 읽음 여부 필터로 알림 목록을 페이징 조회할 수 있다.")
	void test2() {
		// given
		Notification notification = Notification.from(new NotificationContext(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, Map.of()
		));
		PageResult<Notification> pageResult = new PageResult<>(
			List.of(notification), 0, 20, 1, 1
		);
		given(persistencePort.findAllBySubscriberId(1L, false, 0, 20)).willReturn(pageResult);

		// when
		PageResult<NotificationResponse> response = sut.getNotifications(1L, false, 0, 20);

		// then
		assertThat(response.totalElements()).isEqualTo(1);
		assertThat(response.content()).hasSize(1);
		assertThat(response.content().get(0).subscriberId()).isEqualTo(1L);
	}
}
