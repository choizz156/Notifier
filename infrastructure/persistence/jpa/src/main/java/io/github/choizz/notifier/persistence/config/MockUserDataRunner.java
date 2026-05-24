package io.github.choizz.notifier.persistence.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.MockUserEntity;
import io.github.choizz.notifier.persistence.repository.MockUserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Component
public class MockUserDataRunner implements ApplicationRunner {

	private final MockUserJpaRepository mockUserJpaRepository;

	@Transactional
	@Override
	public void run(ApplicationArguments args) {
		if (mockUserJpaRepository.count() > 0) {
			log.info("Mock User 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
			return;
		}

		log.info("Mock User 데이터 100명 초기화를 시작합니다...");
		List<MockUserEntity> users = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			Map<NotificationType, Boolean> settings = new HashMap<>();
			
			int typePattern = i % 4;
			
			for (NotificationType type : NotificationType.values()) {
				boolean isSubscribed = false;
				
				if (typePattern == 0) {
					// 1. 모두 동의 (25명)
					isSubscribed = true;
				} else if (typePattern == 1) {
					// 2. 필수 알림(결제/취소)만 동의 (25명)
					isSubscribed = type == NotificationType.PAYMENT_CONFIRMED || type == NotificationType.CANCELLATION_PROCESSED;
				} else if (typePattern == 2) {
					// 3. 마케팅/기타 알림만 동의 (25명)
					isSubscribed = type != NotificationType.PAYMENT_CONFIRMED && type != NotificationType.CANCELLATION_PROCESSED;
				} else {
					// 4. 모두 거부 (25명)
					isSubscribed = false;
				}
				
				settings.put(type, isSubscribed);
			}
			
			users.add(new MockUserEntity(settings));
		}

		mockUserJpaRepository.saveAll(users);
		log.info("Mock User 100명 데이터 초기화 완료!");
	}
}
