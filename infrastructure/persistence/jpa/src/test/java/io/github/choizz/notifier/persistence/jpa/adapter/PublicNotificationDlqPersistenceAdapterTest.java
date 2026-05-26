package io.github.choizz.notifier.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.choizz.notifier.core.application.dto.DlqRecoveryTarget;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationDlqJpaEntity;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationDlqJpaEntity.DlqStatus;
import io.github.choizz.notifier.persistence.jpa.repository.PublicNotificationDlqRepository;

@ExtendWith(MockitoExtension.class)
class PublicNotificationDlqPersistenceAdapterTest {

	@Mock
	private PublicNotificationDlqRepository repository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private PublicNotificationDlqPersistenceAdapter adapter;

	@Captor
	private ArgumentCaptor<PublicNotificationDlqJpaEntity> entityCaptor;

	@DisplayName("이벤트 객체와 예외가 주어지면 PENDING 상태의 엔티티로 저장한다.")
	@Test
	void test1() {
		// given
		PublicNotificationRequestedEvent event = new PublicNotificationRequestedEvent(
			List.of(1L, 2L), "{}", "NOTICE", "key"
		);
		Exception exception = new RuntimeException("Error");

		// when
		adapter.saveDlq(event, exception);

		// then
		verify(repository).save(entityCaptor.capture());
		PublicNotificationDlqJpaEntity savedEntity = entityCaptor.getValue();
		assertThat(savedEntity.getStatus()).isEqualTo(DlqStatus.PENDING);
		assertThat(savedEntity.getEventPayload()).contains("key");
	}

	@DisplayName("PENDING 상태인 DLQ 목록을 조회하여 이벤트를 파싱하여 반환한다.")
	@Test
	void test2() throws Exception {
		// given
		PublicNotificationRequestedEvent event = new PublicNotificationRequestedEvent(
			List.of(1L), "{}", "NOTICE", "key2"
		);
		String payload = objectMapper.writeValueAsString(event);
		PublicNotificationDlqJpaEntity entity = PublicNotificationDlqJpaEntity.builder()
			.id(1L)
			.eventPayload(payload)
			.status(DlqStatus.PENDING)
			.createdAt(LocalDateTime.now())
			.build();

		when(repository.findByStatusOrderByCreatedAtAsc(any(), any()))
			.thenReturn(List.of(entity));

		// when
		List<DlqRecoveryTarget> targets = adapter.findPendingDlqs(10);

		// then
		assertThat(targets).hasSize(1);
		assertThat(targets.get(0).dlqId()).isEqualTo(1L);
		assertThat(targets.get(0).event().idempotentKey()).isEqualTo("key2");
	}

	@DisplayName("dlqId로 엔티티를 찾아 RESOLVED 상태로 변경한다.")
	@Test
	void test3() {
		// given
		PublicNotificationDlqJpaEntity entity = PublicNotificationDlqJpaEntity.builder()
			.id(1L)
			.status(DlqStatus.PENDING)
			.build();
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		// when
		adapter.markAsResolved(1L);

		// then
		assertThat(entity.getStatus()).isEqualTo(DlqStatus.RESOLVED);
		verify(repository).save(entity);
	}
}
