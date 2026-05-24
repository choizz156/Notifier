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
	maxAttemptsExpression = "${notifier.retry.minimum.max-attempts:2}",         // 총 2번 시도 (최초 1번 + 재시도 1번)
	backoff = @Backoff(
		delayExpression = "${notifier.retry.minimum.delay:1000}",
		multiplierExpression = "${notifier.retry.minimum.multiplier:2.0}",
		maxDelayExpression = "${notifier.retry.minimum.max-delay:5000}"
	)
)
public @interface MinimumRetry {
}
