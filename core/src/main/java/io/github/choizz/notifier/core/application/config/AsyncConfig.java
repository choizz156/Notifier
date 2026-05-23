package io.github.choizz.notifier.core.application.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {

	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {

		SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("publish-async-");
		executor.setVirtualThreads(true);
		return executor;
	}
}
