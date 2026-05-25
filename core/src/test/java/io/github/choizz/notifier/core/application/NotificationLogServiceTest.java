package io.github.choizz.notifier.core.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;

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
		notificationLogService.saveEventLog(1L, EventStatus.SENT, context);

		// then
		verify(notificationLogPersistencePort, times(1)).save(log);
	}

	@DisplayName("PROCESSING 상태로 변경 가능하면 true를 반환한다.")
	@Test
	void test2() {
		// given
		NotificationLog log = NotificationLog.builder()
			.eventStatus(EventStatus.REQUESTED)
			.build();
		when(notificationLogPersistencePort.findLatestByNotificationId(1L)).thenReturn(log);

		// when
		boolean result = notificationLogService.tryClaim(1L);

		// then
		assertThat(result).isTrue();
		assertThat(log.eventStatus()).isEqualTo(EventStatus.PROCESSING);
		verify(notificationLogPersistencePort, times(1)).save(log);
	}

	@DisplayName("이미 PROCESSING 상태이거나 SENT 상태이면 claim에 실패하여 false를 반환한다.")
	@Test
	void test3() {
		// given
		NotificationLog processingLog = NotificationLog.builder().eventStatus(EventStatus.PROCESSING).build();
		when(notificationLogPersistencePort.findLatestByNotificationId(1L)).thenReturn(processingLog);

		NotificationLog sentLog = NotificationLog.builder().eventStatus(EventStatus.SENT).build();
		when(notificationLogPersistencePort.findLatestByNotificationId(2L)).thenReturn(sentLog);

		// when
		boolean result1 = notificationLogService.tryClaim(1L);
		boolean result2 = notificationLogService.tryClaim(2L);

		// then
		assertThat(result1).isFalse();
		assertThat(result2).isFalse();
		verify(notificationLogPersistencePort, never()).save(any());
	}

	@DisplayName("OptimisticLockingFailureException 발생 시 처리 중 상태 변경 실패로 간주하고 false를 반환한다.")
	@Test
	void test4() {
		// given
		NotificationLog log = NotificationLog.builder().eventStatus(EventStatus.REQUESTED).build();
		when(notificationLogPersistencePort.findLatestByNotificationId(1L)).thenReturn(log);
		doThrow(OptimisticLockingFailureException.class).when(notificationLogPersistencePort).save(log);

		// when
		boolean result = notificationLogService.tryClaim(1L);

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
