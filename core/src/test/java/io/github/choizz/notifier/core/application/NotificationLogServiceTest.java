package io.github.choizz.notifier.core.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.factory.NotificationLogFactory;
import io.github.choizz.notifier.core.application.port.out.NotificationLogPersistencePort;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;

@ExtendWith(MockitoExtension.class)
class NotificationLogServiceTest {

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private NotificationLogPersistencePort notificationLogPersistencePort;

	@Mock
	private NotificationLogFactory notificationLogFactory;

	@InjectMocks
	private NotificationLogService notificationLogService;

	@DisplayName("이벤트 로그를 생성하고 저장한다.")
	@Test
	void test1() {
		// given
		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.notificationType("PAYMENT_CONFIRMED")
			.channel("EMAIL")
			.retryCount(0)
			.build();
		NotificationLog log = NotificationLog.builder().id(1L).eventStatus(EventStatus.SENT).build();

		when(notificationLogFactory.create(EventStatus.SENT, context)).thenReturn(log);

		// when
		notificationLogService.saveNotificationLog(1L, EventStatus.SENT, context);

		// then
		verify(notificationLogPersistencePort, times(1)).save(log);
	}

	@DisplayName("PROCESSING 상태로 변경 가능하면 true를 반환한다.")
	@Test
	void test2() {
		// given
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog log = NotificationLog.request(notification);
		when(notificationLogPersistencePort.findLatestByReferenceId(1L, ReferenceType.PERSONAL)).thenReturn(Optional.of(log));

		// when
		boolean result = notificationLogService.tryClaim(1L, ReferenceType.PERSONAL);

		// then
		assertThat(result).isTrue();
		assertThat(log.eventStatus()).isEqualTo(EventStatus.PROCESSING);
		verify(notificationLogPersistencePort, times(1)).save(log);
	}

	@DisplayName("이미 PROCESSING 상태이거나 SENT 상태이면 claim에 실패하여 false를 반환한다.")
	@Test
	void test3() {
		// given
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog processingLog = NotificationLog.request(notification);
		processingLog.markAsProcessing();
		when(notificationLogPersistencePort.findLatestByReferenceId(1L, ReferenceType.PERSONAL)).thenReturn(Optional.of(processingLog));

		PublicationContext sentContext = PublicationContext.builder()
			.notificationId(2L)
			.referenceType(ReferenceType.PERSONAL.name())
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.EMAIL.name())
			.metadata("{}")
			.retryCount(0)
			.build();
		NotificationLog sentLog = NotificationLog.sent(sentContext);
		when(notificationLogPersistencePort.findLatestByReferenceId(2L, ReferenceType.PERSONAL)).thenReturn(Optional.of(sentLog));

		// when
		boolean result1 = notificationLogService.tryClaim(1L, ReferenceType.PERSONAL);
		boolean result2 = notificationLogService.tryClaim(2L, ReferenceType.PERSONAL);

		// then
		assertThat(result1).isFalse();
		assertThat(result2).isFalse();
		verify(notificationLogPersistencePort, never()).save(any());
	}

	@DisplayName("OptimisticLockingFailureException 발생 시 처리 중 상태 변경 실패로 간주하고 false를 반환한다.")
	@Test
	void test4() {
		// given
		Notification notification = Notification.builder()
			.id(1L)
			.subscriberId(2L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.metadata("{}")
			.build();
		NotificationLog log = NotificationLog.request(notification);
		when(notificationLogPersistencePort.findLatestByReferenceId(1L, ReferenceType.PERSONAL)).thenReturn(Optional.of(log));
		doThrow(OptimisticLockingFailureException.class).when(notificationLogPersistencePort).save(log);

		// when
		boolean result = notificationLogService.tryClaim(1L, ReferenceType.PERSONAL);

		// then
		assertThat(result).isFalse();
		verify(notificationLogPersistencePort, times(1)).save(log);
	}

	@DisplayName("처리되지 않은 알림 ID 목록을 조회한다.")
	@Test
	void test5() {
		// given
		when(notificationLogPersistencePort.findUnprocessedNotificationIds(NotificationLogService.targetStatuses, 0L, 10))
			.thenReturn(List.of(1L, 2L, 3L));

		// when
		List<Long> ids = notificationLogService.findUnprocessedNotificationIds(0L, 10);

		// then
		assertThat(ids).containsExactly(1L, 2L, 3L);
	}
}
