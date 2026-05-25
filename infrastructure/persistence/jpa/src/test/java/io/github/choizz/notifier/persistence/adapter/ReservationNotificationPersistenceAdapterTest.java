package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import io.github.choizz.notifier.core.domain.model.ReservationInformation;
import io.github.choizz.notifier.persistence.entity.ReservationNotificationEntity;
import io.github.choizz.notifier.persistence.repository.ReservationNotificationJpaRepository;

@ExtendWith(MockitoExtension.class)
class ReservationNotificationPersistenceAdapterTest {

	@Mock
	private ReservationNotificationJpaRepository reservationNotificationJpaRepository;

	@InjectMocks
	private ReservationNotificationPersistenceAdapter adapter;

	@DisplayName("예약 알림 정보를 단건 저장한다.")
	@Test
	void test1() {
		// given
		ReservationInformation domain = ReservationInformation.builder()
			.subscriberId(1L)
			.build();
			
		ReservationNotificationEntity entity = ReservationNotificationEntity.builder()
			.subscriberId(1L)
			.build();
		entity.id(100L);
			
		when(reservationNotificationJpaRepository.save(any(ReservationNotificationEntity.class))).thenReturn(entity);

		// when
		ReservationInformation result = adapter.save(domain);

		// then
		assertThat(result.id()).isEqualTo(100L);
		verify(reservationNotificationJpaRepository, times(1)).save(any(ReservationNotificationEntity.class));
	}

	@DisplayName("예약 알림 정보를 다건 저장한다.")
	@Test
	void test2() {
		// given
		ReservationInformation domain1 = ReservationInformation.builder().subscriberId(1L).build();
		ReservationInformation domain2 = ReservationInformation.builder().subscriberId(2L).build();

		// when
		adapter.saveAll(List.of(domain1, domain2));

		// then
		verify(reservationNotificationJpaRepository, times(1)).saveAll(any());
	}

	@DisplayName("발행되지 않은 예약 알림 목록을 조회한다.")
	@Test
	void test3() {
		// given
		ReservationNotificationEntity entity = ReservationNotificationEntity.builder().build();
		entity.id(1L);
		
		LocalDateTime time = LocalDateTime.now();
		when(reservationNotificationJpaRepository.findUnpublishedBefore(
			org.mockito.ArgumentMatchers.eq(time),
			org.mockito.ArgumentMatchers.eq(0L),
			any(Limit.class)
		)).thenReturn(List.of(entity));

		// when
		List<ReservationInformation> result = adapter.findUnpublishedNotificationsBefore(time, 0L, 10);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).id()).isEqualTo(1L);
	}

	@DisplayName("예약 알림을 발행 완료 상태로 변경한다.")
	@Test
	void test4() {
		// given
		List<Long> ids = List.of(1L, 2L);

		// when
		adapter.markAsPublished(ids);

		// then
		verify(reservationNotificationJpaRepository, times(1)).markAsPublished(ids);
	}
}
