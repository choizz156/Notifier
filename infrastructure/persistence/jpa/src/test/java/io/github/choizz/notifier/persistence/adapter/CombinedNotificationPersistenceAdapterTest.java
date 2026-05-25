package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import io.github.choizz.notifier.core.application.dto.NotificationResponse;
import io.github.choizz.notifier.core.application.dto.PageResult;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationStatus;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.ReferenceType;
import io.github.choizz.notifier.persistence.repository.CombinedNotificationJpaRepository;
import io.github.choizz.notifier.persistence.repository.CombinedNotificationProjection;

@ExtendWith(MockitoExtension.class)
class CombinedNotificationPersistenceAdapterTest {

	@Mock
	private CombinedNotificationJpaRepository combinedNotificationJpaRepository;

	@InjectMocks
	private CombinedNotificationPersistenceAdapter adapter;

	@DisplayName("isRead 조건 없이 통합 알림 목록을 조회한다.")
	@Test
	void test1() {
		// given
		Long subscriberId = 1L;
		int page = 0;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size);

		CombinedNotificationProjection projection = createMockProjection(
			100L, "PERSONAL", subscriberId, "PAYMENT_CONFIRMED", "EMAIL", "COMPLETED", "메시지", true, LocalDateTime.now(), 0
		);
		Page<CombinedNotificationProjection> mockPage = new PageImpl<>(List.of(projection), pageRequest, 1);

		when(combinedNotificationJpaRepository.findCombinedNotifications(eq(subscriberId), any(PageRequest.class)))
			.thenReturn(mockPage);

		// when
		PageResult<NotificationResponse> result = adapter.loadCombinedNotifications(subscriberId, null, page, size);

		// then
		assertThat(result.totalElements()).isEqualTo(1);
		assertThat(result.content()).hasSize(1);
		NotificationResponse response = result.content().get(0);
		assertThat(response.id()).isEqualTo(100L);
		assertThat(response.referenceType()).isEqualTo(ReferenceType.PERSONAL);
		assertThat(response.notificationType()).isEqualTo(NotificationType.PAYMENT_CONFIRMED);
		assertThat(response.channel()).isEqualTo(Channel.EMAIL);
		assertThat(response.status()).isEqualTo(NotificationStatus.COMPLETED);
		assertThat(response.isRead()).isTrue();
	}

	@DisplayName("isRead 조건이 있을 때 해당 조건으로 통합 알림 목록을 조회한다.")
	@Test
	void test2() {
		// given
		Long subscriberId = 1L;
		int page = 0;
		int size = 10;
		Boolean isRead = false;
		PageRequest pageRequest = PageRequest.of(page, size);

		CombinedNotificationProjection projection = createMockProjection(
			200L, "PUBLIC", subscriberId, "COUPON_ISSUED", "NONE", "COMPLETED", "공지사항", false, LocalDateTime.now(), 0
		);
		Page<CombinedNotificationProjection> mockPage = new PageImpl<>(List.of(projection), pageRequest, 1);

		when(combinedNotificationJpaRepository.findCombinedNotificationsByIsRead(eq(subscriberId), eq(isRead), any(PageRequest.class)))
			.thenReturn(mockPage);

		// when
		PageResult<NotificationResponse> result = adapter.loadCombinedNotifications(subscriberId, isRead, page, size);

		// then
		assertThat(result.totalElements()).isEqualTo(1);
		assertThat(result.content()).hasSize(1);
		NotificationResponse response = result.content().get(0);
		assertThat(response.id()).isEqualTo(200L);
		assertThat(response.referenceType()).isEqualTo(ReferenceType.PUBLIC);
		assertThat(response.notificationType()).isEqualTo(NotificationType.COUPON_ISSUED);
		assertThat(response.channel()).isNull(); // "NONE" -> null
		assertThat(response.status()).isEqualTo(NotificationStatus.COMPLETED);
		assertThat(response.isRead()).isFalse();
	}

	private CombinedNotificationProjection createMockProjection(
		Long id, String referenceType, Long subscriberId, String notificationType,
		String channel, String status, String message, Boolean isRead, LocalDateTime createdAt, Integer retryCount
	) {
		return new CombinedNotificationProjection() {
			@Override public Long getId() { return id; }
			@Override public String getReference_type() { return referenceType; }
			@Override public Long getSubscriber_id() { return subscriberId; }
			@Override public String getNotification_type() { return notificationType; }
			@Override public String getChannel() { return channel; }
			@Override public String getStatus() { return status; }
			@Override public String getMessage() { return message; }
			@Override public Boolean getIs_read() { return isRead; }
			@Override public LocalDateTime getCreated_at() { return createdAt; }
			@Override public Integer getManual_retry_count() { return retryCount; }
		};
	}
}
