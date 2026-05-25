package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.PublicNotification;
import io.github.choizz.notifier.core.domain.model.PublicNotificationReceipt;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationEntity;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationReceiptEntity;
import io.github.choizz.notifier.persistence.jpa.adapter.PublicNotificationPersistenceAdapter;
import io.github.choizz.notifier.persistence.jpa.adapter.PublicNotificationReceiptRepository;
import io.github.choizz.notifier.persistence.jpa.repository.PublicNotificationJpaRepository;

@ExtendWith(MockitoExtension.class)
class PublicNotificationPersistenceAdapterTest {

	@Mock
	private PublicNotificationReceiptRepository publicNotificationReceiptRepository;

	@Mock
	private PublicNotificationJpaRepository publicNotificationJpaRepository;

	@InjectMocks
	private PublicNotificationPersistenceAdapter adapter;

	@DisplayName("공통 알림 수신 영수증을 저장한다.")
	@Test
	void test1() {
		// given
		PublicNotificationReceipt receipt = PublicNotificationReceipt.builder()
			.subscriberId(1L)
			.publicNotificationId(100L)
			.build();

		// when
		adapter.saveReceipt(receipt);

		// then
		verify(publicNotificationReceiptRepository, times(1)).save(any(PublicNotificationReceiptEntity.class));
	}

	@DisplayName("특정 구독자가 공통 알림을 수신했는지 여부를 반환한다.")
	@Test
	void test2() {
		// given
		Long subscriberId = 1L;
		Long publicNotificationId = 100L;
		when(publicNotificationReceiptRepository.existsBySubscriberIdAndPublicNotificationId(subscriberId, publicNotificationId))
			.thenReturn(true);

		// when
		boolean result = adapter.existsReceipt(subscriberId, publicNotificationId);

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("공통 알림을 저장하고 변환하여 반환한다.")
	@Test
	void test3() {
		// given
		PublicNotification notification = PublicNotification.builder()
			.notificationType(NotificationType.PAYMENT_CONFIRMED)
			.metadata("{}")
			.idempotencyKey("key")
			.build();

		PublicNotificationEntity savedEntity = new PublicNotificationEntity(NotificationType.PAYMENT_CONFIRMED, "{}", "key");
		when(publicNotificationJpaRepository.save(any(PublicNotificationEntity.class))).thenReturn(savedEntity);

		// when
		PublicNotification result = adapter.save(notification);

		// then
		assertThat(result.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(result.metadata()).isEqualTo("{}");
		assertThat(result.idempotencyKey()).isEqualTo("key");
		verify(publicNotificationJpaRepository, times(1)).save(any(PublicNotificationEntity.class));
	}
}
