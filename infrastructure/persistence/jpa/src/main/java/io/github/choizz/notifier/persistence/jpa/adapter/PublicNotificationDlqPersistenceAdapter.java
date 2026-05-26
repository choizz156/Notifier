package io.github.choizz.notifier.persistence.jpa.adapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.choizz.notifier.core.application.dto.DlqRecoveryTarget;
import io.github.choizz.notifier.core.application.port.out.DlqPort;
import io.github.choizz.notifier.core.domain.event.PublicNotificationRequestedEvent;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationDlqJpaEntity;
import io.github.choizz.notifier.persistence.jpa.entity.PublicNotificationDlqJpaEntity.DlqStatus;
import io.github.choizz.notifier.persistence.jpa.repository.PublicNotificationDlqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PublicNotificationDlqPersistenceAdapter implements DlqPort {

	private final PublicNotificationDlqRepository repository;
	private final ObjectMapper objectMapper;

	public void saveDlq(PublicNotificationRequestedEvent event, Exception exception) {
		String payloadString = null;
		if (event != null) {
			try {
				payloadString = objectMapper.writeValueAsString(event);
			} catch (JsonProcessingException e) {
				log.error("DLQ 이벤트 페이로드 직렬화 실패 - 이벤트 ID: {}", event.idempotentKey(), e);
				payloadString = event.toString();
			}
		}

		PublicNotificationDlqJpaEntity entity = PublicNotificationDlqJpaEntity.builder()
			.errorMessage(exception.getMessage())
			.eventPayload(payloadString)
			.status(DlqStatus.PENDING)
			.createdAt(LocalDateTime.now())
			.build();

		repository.save(entity);
		log.info("DLQ 저장 완료 - 이벤트 ID: {}", event != null ? event.idempotentKey() : "알수없음");
	}

	public List<DlqRecoveryTarget> findPendingDlqs(int limit) {
		List<PublicNotificationDlqJpaEntity> entities = repository.findByStatusOrderByCreatedAtAsc(
			DlqStatus.PENDING, PageRequest.of(0, limit)
		);

		List<DlqRecoveryTarget> targets = new ArrayList<>();
		for (PublicNotificationDlqJpaEntity entity : entities) {
			if (entity.getEventPayload() == null) {
				continue;
			}
			try {
				PublicNotificationRequestedEvent event = objectMapper.readValue(
					entity.getEventPayload(), PublicNotificationRequestedEvent.class
				);
				targets.add(DlqRecoveryTarget.builder()
					.dlqId(entity.getId())
					.event(event)
					.build());
			} catch (JsonProcessingException e) {
				log.error("DLQ 이벤트 페이로드 역직렬화 실패 - DLQ ID: {}", entity.getId(), e);
			}
		}
		return targets;
	}

	public void markAsResolved(Long dlqId) {
		repository.findById(dlqId).ifPresent(entity -> {
			entity.markAsResolved();
			repository.save(entity);
		});
	}
}
