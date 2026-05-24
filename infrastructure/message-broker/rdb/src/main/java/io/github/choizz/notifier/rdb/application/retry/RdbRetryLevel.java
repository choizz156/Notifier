package io.github.choizz.notifier.rdb.application.retry;

import java.util.Arrays;
import java.util.Set;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RdbRetryLevel {

	AGGRESSIVE(
		Set.of(NotificationType.PAYMENT_CONFIRMED, NotificationType.CANCELLATION_PROCESSED),
		8, 3900 // 최대 8회, 1시간 + 버퍼 5분
	),
	STANDARD(
		Set.of(NotificationType.COUPON_ISSUED, NotificationType.COUPON_EXPIRY_REMINDER),
		3, 30 // 최대 3회, 3초 + 버퍼 27초
	),
	MINIMUM(
		Set.of(NotificationType.COURSE_START_REMINDER, NotificationType.NEW_LECTURE_OPENED),
		2, 10 // 최대 2회, 1초 + 버퍼 9초
	),
	NONE(
		Set.of(),
		1, 5 // 1회, 버퍼 5초
	);

	private final Set<NotificationType> supportedTypes;
	private final int maxAttempts;
	private final long maxProcessingTimeSeconds;

	public static RdbRetryLevel from(NotificationType type) {
		return Arrays.stream(values())
			.filter(level -> level.supportedTypes.contains(type))
			.findFirst()
			.orElse(NONE);
	}
}
