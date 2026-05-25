package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.Notification;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.NotificationEntity;
import io.github.choizz.notifier.persistence.repository.NotificationJpaRepository;

@ExtendWith(MockitoExtension.class)
class NotificationPersistenceAdapterTest {

	@Mock
	private NotificationJpaRepository notificationJpaRepository;

	@InjectMocks
	private NotificationPersistenceAdapter adapter;

	@DisplayName("단일 알림을 저장한다.")
	@Test
	void test1() {
		// given
		Notification domain = Notification.builder()
			.subscriberId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.status(NotificationStatus.PENDING)
			.build();
			
		NotificationEntity entity = NotificationEntity.builder()
			.subscriberId(1L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.channel(Channel.EMAIL)
			.status(NotificationStatus.PENDING)
			.build();
		entity.id(100L);

		when(notificationJpaRepository.save(any(NotificationEntity.class))).thenReturn(entity);

		// when
		Notification saved = adapter.save(domain);

		// then
		assertThat(saved.id()).isEqualTo(100L);
		assertThat(saved.subscriberId()).isEqualTo(1L);
		verify(notificationJpaRepository, times(1)).save(any(NotificationEntity.class));
	}

	@DisplayName("ID로 알림을 찾는다.")
	@Test
	void test2() {
		// given
		NotificationEntity entity = NotificationEntity.builder().build();
		entity.id(1L);
		when(notificationJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

		// when
		Notification result = adapter.findById(1L);

		// then
		assertThat(result.id()).isEqualTo(1L);
	}

	@DisplayName("ID로 알림을 찾을 때 없으면 NoSuchElementException을 발생시킨다.")
	@Test
	void test3() {
		// given
		when(notificationJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adapter.findById(1L))
			.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("중복 알림 존재 여부를 확인한다.")
	@Test
	void test4() {
		// given
		when(notificationJpaRepository.existsBySubscriberIdAndNotificationTypeAndChannelAndStatus(
			1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, NotificationStatus.PENDING))
			.thenReturn(true);

		// when
		boolean exists = adapter.existsDuplicate(1L, NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL);

		// then
		assertThat(exists).isTrue();
	}

	@DisplayName("읽지 않은 알림 목록을 페이징 조회한다.")
	@Test
	void test5() {
		// given
		NotificationEntity entity = NotificationEntity.builder().build();
		entity.id(1L);
		Page<NotificationEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
		
		when(notificationJpaRepository.findBySubscriberIdAndIsRead(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(false), any(Pageable.class)))
			.thenReturn(page);

		// when
		PageResult<Notification> result = adapter.findAllBySubscriberId(1L, false, 0, 10);

		// then
		assertThat(result.content()).hasSize(1);
		assertThat(result.totalElements()).isEqualTo(1);
	}
}
