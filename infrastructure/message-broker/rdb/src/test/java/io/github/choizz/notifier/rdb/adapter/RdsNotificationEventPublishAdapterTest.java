package io.github.choizz.notifier.rdb.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.in.NotificationLogUseCase;
import io.github.choizz.notifier.core.application.port.out.NotifierPort;
import io.github.choizz.notifier.core.domain.event.PublishCommandEvent;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.infrastructure.messagebroker.NotificationDispatcher;
import io.github.choizz.notifier.infrastructure.messagebroker.NotifierFacade;

@ExtendWith(MockitoExtension.class)
class RdsNotificationEventPublishAdapterTest {

	@Mock
	private NotifierFacade notifierFacade;

	@Mock
	private NotificationDispatcher notificationDispatcher;

	@Mock
	private NotificationLogUseCase notificationLogUseCase;

	@InjectMocks
	private RdsNotificationEventPublishAdapter adapter;

	@DisplayName("알림 이벤트를 발행한다.")
	@Test
	void test1() {
		// given
		PublishCommandEvent event = new PublishCommandEvent(
			1L,
			100L,
			NotificationType.PAYMENT_CONFIRMED.name(),
			Channel.EMAIL.name(),
			"{}",
			ReferenceType.PERSONAL.name()
		);

		when(notificationLogUseCase.tryClaim(eq(1L), any())).thenReturn(true);
		NotifierPort notifierPort = mock(NotifierPort.class);
		when(notifierFacade.getNotifierPort(Channel.EMAIL.name())).thenReturn(notifierPort);

		// when
		adapter.publish(event);

		// then
		verify(notificationLogUseCase, times(1)).tryClaim(eq(1L), any());
		verify(notifierFacade, times(1)).getNotifierPort(Channel.EMAIL.name());
		verify(notificationDispatcher, times(1)).dispatch(eq(notifierPort), any(PublicationContext.class));
	}

	@DisplayName("처리 중인 알람에 대한 이벤트 발행은 실패한다.")
	@Test
	void test2() {
		// given
		PublishCommandEvent event = new PublishCommandEvent(
			1L,
			100L,
			NotificationType.PAYMENT_CONFIRMED.name(),
			Channel.EMAIL.name(),
			"{}",
			ReferenceType.PERSONAL.name()
		);

		when(notificationLogUseCase.tryClaim(eq(1L), any())).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> adapter.publish(event))
			.isInstanceOf(OptimisticLockingFailureException.class)
			.hasMessage("이미 처리 중인 알람입니다.");
	}
}
