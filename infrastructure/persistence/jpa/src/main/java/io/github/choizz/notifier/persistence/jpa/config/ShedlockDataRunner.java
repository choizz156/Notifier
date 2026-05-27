package io.github.choizz.notifier.persistence.jpa.config;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.persistence.jpa.entity.ShedlockEntity;
import io.github.choizz.notifier.persistence.jpa.repository.ShedlockJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShedlockDataRunner implements ApplicationRunner {

	private final ShedlockJpaRepository shedlockJpaRepository;

	@Transactional
	@Override
	public void run(ApplicationArguments args) {

		log.info("ShedLock 초기 데이터 삽입을 시작합니다...");

		LocalDateTime now = LocalDateTime.now();
		// 이미 지나간 과거 시간으로 세팅하여 즉시 락을 획득할 수 있도록 함
		LocalDateTime lockUntil = now.minusDays(1);

		ShedlockEntity lock1 = new ShedlockEntity(
			"publishReservationNotification",
			lockUntil,
			now,
			"init"
		);

		ShedlockEntity lock2 = new ShedlockEntity(
			"recoverStuckEvents",
			lockUntil,
			now,
			"init"
		);

		ShedlockEntity lock3 = new ShedlockEntity(
			"recoverUnprocessedNotifications",
			lockUntil,
			now,
			"init"
		);

		shedlockJpaRepository.save(lock1);
		shedlockJpaRepository.save(lock2);
		shedlockJpaRepository.save(lock3);

		log.info("ShedLock 초기 데이터 3건 삽입 완료!");
	}
}
