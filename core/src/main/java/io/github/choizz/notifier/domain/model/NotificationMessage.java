package io.github.choizz.notifier.domain.model;

import java.util.Arrays;
import java.util.Map;

public enum NotificationMessage {
	ENROLLMENT_COMPLETED_EMAIL(NotificationType.ENROLLMENT_COMPLETED, Channel.EMAIL, "[학습 플랫폼] {courseName} 수강 신청이 완료되었습니다."),
	ENROLLMENT_COMPLETED_IN_APP(NotificationType.ENROLLMENT_COMPLETED, Channel.IN_APP, "수강 신청 완료! {courseName} 강의를 확인해보세요."),

	PAYMENT_CONFIRMED_EMAIL(NotificationType.PAYMENT_CONFIRMED, Channel.EMAIL, "[학습 플랫폼] 결제가 확정되었습니다. 주문번호: {orderId}"),
	PAYMENT_CONFIRMED_IN_APP(NotificationType.PAYMENT_CONFIRMED, Channel.IN_APP, "결제 확정! {amount}원 결제가 완료되었습니다."),

	COURSE_START_REMINDER_EMAIL(NotificationType.COURSE_START_REMINDER, Channel.EMAIL, "[학습 플랫폼] 내일부터 {courseName} 강의가 시작됩니다."),
	COURSE_START_REMINDER_IN_APP(NotificationType.COURSE_START_REMINDER, Channel.IN_APP, "강의 시작 D-1! {courseName} 준비되셨나요?"),

	CANCELLATION_PROCESSED_EMAIL(NotificationType.CANCELLATION_PROCESSED, Channel.EMAIL, "[학습 플랫폼] {courseName} 취소 처리가 완료되었습니다."),
	CANCELLATION_PROCESSED_IN_APP(NotificationType.CANCELLATION_PROCESSED, Channel.IN_APP, "취소 처리 완료: {courseName}");

	private final NotificationType type;
	private final Channel channel;
	private final String template;

	NotificationMessage(NotificationType type, Channel channel, String template) {
		this.type = type;
		this.channel = channel;
		this.template = template;
	}

	public static String generate(NotificationType type, Channel channel, Map<String, String> metadata) {
		return Arrays.stream(values())
			.filter(m -> m.type == type && m.channel == channel)
			.findFirst()
			.map(m -> {
				String message = m.template;
				for (Map.Entry<String, String> entry : metadata.entrySet()) {
					message = message.replace("{" + entry.getKey() + "}", entry.getValue());
				}
				return message;
			})
			.orElseThrow(() -> new IllegalArgumentException("해당 조건의 메시지 템플릿이 없습니다."));
	}
}
