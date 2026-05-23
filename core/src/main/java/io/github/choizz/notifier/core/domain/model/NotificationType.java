package io.github.choizz.notifier.core.domain.model;

import static io.github.choizz.notifier.core.domain.model.NotificationType.RetryLevel.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum NotificationType {

	PAYMENT_CONFIRMED("결제 완료 안내", AGGRESSIVE),
	CANCELLATION_PROCESSED("취소 처리 완료 안내", AGGRESSIVE),
	COUPON_ISSUED("신규 쿠폰 발급 안내", STANDARD),
	COUPON_EXPIRY_REMINDER("쿠폰 만료 예정 안내", STANDARD),
	COURSE_START_REMINDER("강의 시작 안내", MINIMUM),
	NEW_LECTURE_OPENED("새로운 강의 오픈 안내", MINIMUM);

	private final String title;
	private final RetryLevel retryLevel;

	public enum RetryLevel {
		AGGRESSIVE,
		STANDARD,
		MINIMUM,
		NONE
	}
}
