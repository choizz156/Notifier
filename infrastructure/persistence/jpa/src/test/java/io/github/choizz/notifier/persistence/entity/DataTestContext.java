package io.github.choizz.notifier.persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.choizz.notifier.persistence.jpa.repository.NotificationLogJpaRepository;
import io.github.choizz.notifier.persistence.jpa.repository.NotificationJpaRepository;

@SpringBootApplication(scanBasePackages = "io.github.choizz.notifier.persistence")
@EnableJpaRepositories("io.github.choizz.notifier.persistence.repository")
@EntityScan("io.github.choizz.notifier.persistence.entity")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataTestContext {

	@Autowired
	protected NotificationLogJpaRepository notificationLogJpaRepository;

	@Autowired
	protected NotificationJpaRepository notificationJpaRepository;

	@AfterEach
	void tearDown() {
		notificationLogJpaRepository.deleteAll();
		notificationJpaRepository.deleteAll();
	}
}
