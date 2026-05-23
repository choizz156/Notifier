package io.github.choizz.notifier.persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.choizz.notifier.persistence.repository.NotificationEventLogJpaRepository;
import io.github.choizz.notifier.persistence.repository.NotificationJpaRepository;

@SpringBootApplication(scanBasePackages = "io.github.choizz.notifier.persistence")
@EnableJpaRepositories("io.github.choizz.notifier.persistence.repository")
@EntityScan("io.github.choizz.notifier.persistence.entity")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataTestContext {

	@Autowired
	protected NotificationEventLogJpaRepository notificationEventLogJpaRepository;

	@Autowired
	protected NotificationJpaRepository notificationJpaRepository;

	@AfterEach
	void tearDown() {
		notificationEventLogJpaRepository.deleteAll();
		notificationJpaRepository.deleteAll();
	}
}
