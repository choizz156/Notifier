package io.github.choizz.notifier.core.domain.model;

import java.util.Arrays;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum RetryLevel {

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

	public static RetryLevel from(NotificationType type) {
		return Arrays.stream(values())
			.filter(level -> level.supportedTypes.contains(type))
			.findFirst()
			.orElse(NONE);
	}
}
