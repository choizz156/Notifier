package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import io.github.choizz.notifier.core.domain.model.EventStatus;
import io.github.choizz.notifier.core.domain.model.NotificationLog;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.NotificationEventLogEntity;
import io.github.choizz.notifier.persistence.repository.NotificationEventLogJpaRepository;

@ExtendWith(MockitoExtension.class)
class NotificationLogPersistenceAdapterTest {

	@Mock
	private NotificationEventLogJpaRepository eventLogJpaRepository;

	@InjectMocks
	private NotificationLogPersistenceAdapter adapter;

	@DisplayName("알림 이벤트 이력을 단건 저장한다.")
	@Test
	void test1() {
		// given
		NotificationLog domain = NotificationLog.builder()
			.notificationId(10L)
			.eventStatus(EventStatus.SENT)
			.build();
			
		NotificationEventLogEntity entity = NotificationEventLogEntity.builder()
			.notificationId(10L)
			.eventStatus(EventStatus.SENT)
			.build();
			
		when(eventLogJpaRepository.save(any(NotificationEventLogEntity.class))).thenReturn(entity);

		// when
		adapter.save(domain);

		// then
		verify(eventLogJpaRepository, times(1)).save(any(NotificationEventLogEntity.class));
	}

	@DisplayName("알림 이벤트 이력을 다건 저장한다.")
	@Test
	void test2() {
		// given
		NotificationLog domain1 = NotificationLog.builder().notificationId(10L).eventStatus(EventStatus.SENT).build();
		NotificationLog domain2 = NotificationLog.builder().notificationId(20L).eventStatus(EventStatus.SENT).build();

		// when
		adapter.saveAll(List.of(domain1, domain2));

		// then
		verify(eventLogJpaRepository, times(1)).saveAll(any());
	}

	@DisplayName("가장 최신의 알림 이벤트 이력을 조회한다.")
	@Test
	void test3() {
		// given
		NotificationEventLogEntity entity = NotificationEventLogEntity.builder().notificationId(10L).build();
		when(eventLogJpaRepository.findFirstByNotificationIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.of(entity));

		// when
		NotificationLog result = adapter.findLatestByNotificationId(10L);

		// then
		assertThat(result.notificationId()).isEqualTo(10L);
	}

	@DisplayName("알림 이벤트 이력이 없으면 예외가 발생한다.")
	@Test
	void test4() {
		// given
		when(eventLogJpaRepository.findFirstByNotificationIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adapter.findLatestByNotificationId(10L))
			.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("처리되지 않은 알림 ID 목록을 조회한다.")
	@Test
	void test5() {
		// given
		when(eventLogJpaRepository.findUnprocessedNotificationIds(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.eq(0L), any(Limit.class)))
			.thenReturn(List.of(1L, 2L, 3L));

		// when
		List<Long> ids = adapter.findUnprocessedNotificationIds(List.of(EventStatus.REQUESTED), 0L, 10);

		// then
		assertThat(ids).containsExactly(1L, 2L, 3L);
	}

	@DisplayName("멈춰있는(Stuck) 로그 목록을 조회한다.")
	@Test
	void test6() {
		// given
		NotificationEventLogEntity entity = NotificationEventLogEntity.builder().build();
		when(eventLogJpaRepository.findStuckLogs(
			org.mockito.ArgumentMatchers.anyLong(),
			org.mockito.ArgumentMatchers.any(EventStatus.class),
			org.mockito.ArgumentMatchers.anyCollection(),
			org.mockito.ArgumentMatchers.any(LocalDateTime.class),
			any(Limit.class)
		)).thenReturn(List.of(entity));

		// when
		List<NotificationLog> result = adapter.findStuckLogs(0L, EventStatus.PROCESSING, List.of(NotificationType.PAYMENT_CONFIRMED), LocalDateTime.now(), 10);

		// then
		assertThat(result).hasSize(1);
	}
}
