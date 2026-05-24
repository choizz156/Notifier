package io.github.choizz.notifier.core.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.github.choizz.notifier.core.domain.model.RetryLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "notifier.retry")
public class RetryProperties {

	private final RetryConfig aggressive = new RetryConfig();
	private final RetryConfig standard = new RetryConfig();
	private final RetryConfig minimum = new RetryConfig();

	@Getter
	@Setter
	public static class RetryConfig {
		private int maxAttempts;
		private long maxProcessingTimeSeconds;
		private long delay;
		private long maxDelay;
		private double multiplier;
	}

	public RetryConfig getConfig(RetryLevel level) {
		return switch (level) {
			case AGGRESSIVE -> aggressive;
			case STANDARD -> standard;
			case MINIMUM -> minimum;
			case NONE -> new RetryConfig() {{
				setMaxAttempts(1);
				setMaxProcessingTimeSeconds(1);
			}};
		};
	}
}
