package io.github.choizz.notifier.rdb.application.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
	maxAttempts = 8, // 총 8번 시도 (최초 1번 + 재시도 7번)
	backoff = @Backoff(delay = 1000, multiplier = 3.715, maxDelay = 3600000) // 한 시간동안
)
public @interface AggressiveRetry {
}
