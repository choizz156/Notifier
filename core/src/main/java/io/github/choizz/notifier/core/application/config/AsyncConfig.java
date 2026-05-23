package io.github.choizz.notifier.core.application.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {

	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {
		return new TaskExecutorAdapter(
			new VirtualThreadTaskExecutor("publish-async-")
		);
	}
}
