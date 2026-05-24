package io.github.choizz.notifier.persistence.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.application.port.out.MockUserPersistencePort;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.MockUserEntity;
import io.github.choizz.notifier.persistence.repository.MockUserJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class MockUserPersistenceAdapter implements MockUserPersistencePort {

	private final MockUserJpaRepository mockUserJpaRepository;

	@Override
	public boolean isSubscribed(Long userId, NotificationType type) {
		return mockUserJpaRepository.findById(userId)
			.map(MockUserEntity::notificationSettings)
			.map(settings -> settings.getOrDefault(type, false))
			.orElse(false);
	}

	@Override
	public List<NotificationType> findSubscribedTypes(Long userId) {
		return mockUserJpaRepository.findById(userId)
			.map(MockUserEntity::notificationSettings)
			.map(settings -> settings.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toList()
			)
			.orElse(Collections.emptyList());
	}
}
