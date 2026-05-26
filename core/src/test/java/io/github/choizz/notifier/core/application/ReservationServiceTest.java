package io.github.choizz.notifier.core.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.dto.NotificationContext;
import io.github.choizz.notifier.core.application.port.in.PublicNotificationUseCase;
import io.github.choizz.notifier.core.application.port.out.ReservationNotificationPersistencePort;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReservationInformation;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	@Mock
	private ReservationNotificationPersistencePort reservationNotificationPersistencePort;

	@Mock
	private PublicNotificationUseCase publicNotificationUseCase;

	@InjectMocks
	private ReservationService reservationService;

	@DisplayName("공개 예약 알림 생성 요청 시 ReservationInformation을 저장한다.")
	@Test
	void test1() {
		// given
		LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

		// when
		reservationService.reservePublic(NotificationType.NEW_LECTURE_OPENED, Map.of("key", "value"), futureTime);

		// then
		verify(reservationNotificationPersistencePort, times(1)).saveAll(anyList());
	}

	@DisplayName("예약 알림 발행 시, 공개 알림을 유스케이스로 발행하고 상태를 업데이트한다.")
	@Test
	void test2() {
		// given
		ReservationInformation info1 = ReservationInformation.builder()
			.id(1L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.metadata("{\"key\":\"value\"}")
			.build();
		ReservationInformation info2 = ReservationInformation.builder()
			.id(2L)
			.notificationType(NotificationType.NEW_LECTURE_OPENED)
			.metadata("{}")
			.build();

		when(reservationNotificationPersistencePort.findUnpublishedNotificationsBefore(any(LocalDateTime.class), any(Long.class), anyInt()))
			.thenReturn(List.of(info1, info2))
			.thenReturn(List.of());

		// when
		reservationService.publishReservationNotification();

		// then
		verify(publicNotificationUseCase, times(2)).pushToPublic(any(NotificationContext.class));
		verify(reservationNotificationPersistencePort, times(1)).markAsPublished(List.of(1L, 2L));
	}

	@DisplayName("일부 알림 발송이 실패해도 성공한 알림은 상태를 업데이트한다.")
	@Test
	void test3() {
		// given
		ReservationInformation info1 = ReservationInformation.builder()
			.id(1L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.metadata("{\"key\":\"value\"}")
			.build();
		ReservationInformation info2 = ReservationInformation.builder()
			.id(2L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.metadata("{}")
			.build();

		when(reservationNotificationPersistencePort.findUnpublishedNotificationsBefore(any(LocalDateTime.class), any(Long.class), anyInt()))
			.thenReturn(List.of(info1, info2))
			.thenReturn(List.of());

		// 첫번째 알림 발송은 예외 발생, 두번째는 성공
		doThrow(new RuntimeException("error"))
			.doNothing()
			.when(publicNotificationUseCase).pushToPublic(any(NotificationContext.class));

		// when
		reservationService.publishReservationNotification();

		// then
		verify(publicNotificationUseCase, times(2)).pushToPublic(any(NotificationContext.class));
		verify(reservationNotificationPersistencePort, times(1)).markAsPublished(List.of(2L)); // 2번만 성공
	}

	@DisplayName("메타데이터 JSON 파싱 중 예외가 발생해도 다른 알림 발행에 영향을 주지 않는다.")
	@Test
	void test4() {
		// given
		ReservationInformation info1 = ReservationInformation.builder()
			.id(1L)
			.notificationType(NotificationType.COUPON_ISSUED)
			.metadata("{invalid-json}")
			.build();
		ReservationInformation info2 = ReservationInformation.builder()
			.id(2L)
			.notificationType(NotificationType.NEW_LECTURE_OPENED)
			.metadata("{\"key\":\"value\"}")
			.build();

		when(reservationNotificationPersistencePort.findUnpublishedNotificationsBefore(any(LocalDateTime.class), any(Long.class), anyInt()))
			.thenReturn(List.of(info1, info2))
			.thenReturn(List.of());

		// when
		reservationService.publishReservationNotification();

		// then
		verify(publicNotificationUseCase, times(1)).pushToPublic(any(NotificationContext.class));
		verify(reservationNotificationPersistencePort, times(1)).markAsPublished(List.of(2L));
	}
}

