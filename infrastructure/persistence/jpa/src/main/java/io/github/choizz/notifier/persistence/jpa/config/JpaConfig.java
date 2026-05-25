package io.github.choizz.notifier.persistence.jpa.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "io.github.choizz.notifier.persistence.jpa")
@EntityScan(basePackages = "io.github.choizz.notifier.persistence.jpa")
@Configuration
public class JpaConfig {

}
