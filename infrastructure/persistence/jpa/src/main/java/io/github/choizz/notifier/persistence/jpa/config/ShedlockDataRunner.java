package io.github.choizz.notifier.persistence.jpa.config;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.choizz.notifier.persistence.jpa.entity.ShedlockEntity;
import io.github.choizz.notifier.persistence.jpa.repository.ShedlockJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Component
public class ShedlockDataRunner implements ApplicationRunner {

	private final ShedlockJpaRepository shedlockJpaRepository;

	@Transactional
	@Override
	public void run(ApplicationArguments args) {
		if (shedlockJpaRepository.count() > 0) {
			log.info("ShedLock 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
			return;
		}

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

		shedlockJpaRepository.save(lock1);
		shedlockJpaRepository.save(lock2);

		log.info("ShedLock 초기 데이터 2건 삽입 완료!");
	}
}
