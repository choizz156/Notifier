package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.jpa.entity.MockUserEntity;
import io.github.choizz.notifier.persistence.jpa.adapter.MockUserPersistenceAdapter;
import io.github.choizz.notifier.persistence.jpa.repository.MockUserJpaRepository;

@ExtendWith(MockitoExtension.class)
class MockUserPersistenceAdapterTest {

	@Mock
	private MockUserJpaRepository mockUserJpaRepository;

	@InjectMocks
	private MockUserPersistenceAdapter adapter;

	@DisplayName("사용자의 알림 타입 구독 여부를 확인한다.")
	@Test
	void test1() {
		// given
		MockUserEntity user = new MockUserEntity(
			Map.of(NotificationType.PAYMENT_CONFIRMED, true),
			Map.of()
		);
		org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);
		
		when(mockUserJpaRepository.findById(1L)).thenReturn(Optional.of(user));

		// when
		boolean result = adapter.isSubscribed(1L, NotificationType.PAYMENT_CONFIRMED);

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("사용자의 구독된 알림 타입 목록을 조회한다.")
	@Test
	void test2() {
		// given
		MockUserEntity user = new MockUserEntity(
			Map.of(
				NotificationType.PAYMENT_CONFIRMED, true,
				NotificationType.COUPON_ISSUED, false
			),
			Map.of()
		);
		org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);
		
		when(mockUserJpaRepository.findById(1L)).thenReturn(Optional.of(user));

		// when
		List<NotificationType> types = adapter.findSubscribedTypes(1L);

		// then
		assertThat(types).containsExactly(NotificationType.PAYMENT_CONFIRMED);
	}

	@DisplayName("사용자의 구독된 채널 목록을 조회한다.")
	@Test
	void test3() {
		// given
		MockUserEntity user = new MockUserEntity(
			Map.of(),
			Map.of(
				Channel.EMAIL, true,
				Channel.IN_APP, false
			)
		);
		org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);
		
		when(mockUserJpaRepository.findById(1L)).thenReturn(Optional.of(user));

		// when
		Set<Channel> channels = adapter.findSubscribedChannels(1L);

		// then
		assertThat(channels).containsExactly(Channel.EMAIL);
	}
}
