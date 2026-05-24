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
	maxAttemptsExpression = "${notifier.retry.standard.max-attempts:3}",         // 총 3번 시도 (최초 1번 + 재시도 2번)
	backoff = @Backoff(
		delayExpression = "${notifier.retry.standard.delay:1000}",
		multiplierExpression = "${notifier.retry.standard.multiplier:2.0}",
		maxDelayExpression = "${notifier.retry.standard.max-delay:10000}"
	)
)
public @interface StandardRetry {

}
