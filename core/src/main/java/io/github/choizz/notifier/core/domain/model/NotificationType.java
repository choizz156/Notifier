package io.github.choizz.notifier.core.domain.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum NotificationType {

	PAYMENT_CONFIRMED("결제 완료 안내"),
	CANCELLATION_PROCESSED("취소 처리 완료 안내"),
	COUPON_ISSUED("신규 쿠폰 발급 안내"),
	COUPON_EXPIRY_REMINDER("쿠폰 만료 예정 안내"),
	COURSE_START_REMINDER("강의 시작 안내"),
	NEW_LECTURE_OPENED("새로운 강의 오픈 안내");

	private final String title;
}
