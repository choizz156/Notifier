package io.github.choizz.notifier.rdb.application.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import io.github.choizz.notifier.core.application.port.out.NotificationRetryPolicyPort;
import io.github.choizz.notifier.core.domain.model.NotificationType.RetryLevel;
import io.github.choizz.notifier.rdb.application.retry.RetryProcessor;

@Component
public class NotificationRetryPolicyAdapter implements NotificationRetryPolicyPort {

	private static final int BUFFER_SECONDS = 10;
	private final Map<RetryLevel, Policy> policies = new EnumMap<>(RetryLevel.class);

	public NotificationRetryPolicyAdapter(List<RetryProcessor> retryProcessors) {
		
		policies.put(RetryLevel.NONE, new Policy(1, BUFFER_SECONDS));

		for (RetryProcessor processor : retryProcessors) {
			RetryLevel level = processor.getRetryLevel();
			
			Retryable retryable = extractRetryable(processor);
			if (retryable != null) {
				int maxAttempts = retryable.maxAttempts();
				long maxProcessingTimeSeconds = calculateMaxProcessingTime(retryable) + BUFFER_SECONDS;
				policies.put(level, new Policy(maxAttempts, maxProcessingTimeSeconds));
			}
		}
	}

	private Retryable extractRetryable(RetryProcessor processor) {
		try {
			Method handleMethod = processor.getClass().getMethod("handle", 
				io.github.choizz.notifier.core.application.port.out.NotifierPort.class, 
				io.github.choizz.notifier.core.application.dto.PublicationContext.class);
			
			return AnnotationUtils.findAnnotation(handleMethod, Retryable.class);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private long calculateMaxProcessingTime(Retryable retryable) {
		Backoff backoff = retryable.backoff();
		int maxAttempts = retryable.maxAttempts();
		
		long delay = backoff.delay() == 0 ? 1000 : backoff.delay();
		double multiplier = backoff.multiplier() > 0 ? backoff.multiplier() : 1.0;
		long maxDelay = backoff.maxDelay() == 0 ? 0 : backoff.maxDelay();

		long totalWaitMs = 0;
		long currentDelay = delay;

		for (int i = 1; i < maxAttempts; i++) {
			totalWaitMs += currentDelay;
			currentDelay = (long) (currentDelay * multiplier);
			if (maxDelay > 0 && currentDelay > maxDelay) {
				currentDelay = maxDelay;
			}
		}
		
		return totalWaitMs / 1000;
	}

	@Override
	public int getMaxAttempts(RetryLevel retryLevel) {
		return policies.getOrDefault(retryLevel, policies.get(RetryLevel.NONE)).maxAttempts;
	}

	@Override
	public long getMaxProcessingTimeSeconds(RetryLevel retryLevel) {
		return policies.getOrDefault(retryLevel, policies.get(RetryLevel.NONE)).maxProcessingTimeSeconds;
	}

	private record Policy(int maxAttempts, long maxProcessingTimeSeconds) {}
}
