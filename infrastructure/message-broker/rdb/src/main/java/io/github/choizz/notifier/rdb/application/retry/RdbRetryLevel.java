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
		Set.of(NotificationType.PAYMENT_CONFIRMED, NotificationType.CANCELLATION_PROCESSED)
	),
	STANDARD(
		Set.of(NotificationType.COUPON_ISSUED, NotificationType.COUPON_EXPIRY_REMINDER)
	),
	MINIMUM(
		Set.of(NotificationType.COURSE_START_REMINDER, NotificationType.NEW_LECTURE_OPENED)
	),
	NONE(
		Set.of()
	);

	private final Set<NotificationType> supportedTypes;

	public static RdbRetryLevel from(NotificationType type) {
		return Arrays.stream(values())
			.filter(level -> level.supportedTypes.contains(type))
			.findFirst()
			.orElse(NONE);
	}
}
