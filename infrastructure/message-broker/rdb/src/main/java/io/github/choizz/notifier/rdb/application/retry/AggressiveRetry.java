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
	maxAttemptsExpression = "${notifier.retry.aggressive.max-attempts:8}", // 총 8번 시도 (최초 1번 + 재시도 7번)
	backoff = @Backoff(
		delayExpression = "${notifier.retry.aggressive.delay:1000}",
		multiplierExpression = "${notifier.retry.aggressive.multiplier:3.715}",
		maxDelayExpression = "${notifier.retry.aggressive.max-delay:3600000}"
	) // 한 시간동안
)
public @interface AggressiveRetry {
}
