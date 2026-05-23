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
	maxAttempts = 3,         // 총 3번 시도 (최초 1번 + 재시도 2번)
	backoff = @Backoff(
		delay = 60000        // 고정적으로 1분 대기
	)
)
public @interface StandardRetry {

}
