import os

templates = {
    "email": {
        "ENROLLMENT_COMPLETED.txt": "[학습 플랫폼] {courseName} 수강 신청이 완료되었습니다.",
        "PAYMENT_CONFIRMED.txt": "[학습 플랫폼] 결제가 확정되었습니다. 주문번호: {orderId}",
        "COURSE_START_REMINDER.txt": "[학습 플랫폼] 내일부터 {courseName} 강의가 시작됩니다.",
        "CANCELLATION_PROCESSED.txt": "[학습 플랫폼] {courseName} 취소 처리가 완료되었습니다.",
        "NEW_LECTURE_OPENED.txt": "[학습 플랫폼] {courseName}의 새로운 강의가 오픈되었습니다.",
        "COUPON_ISSUED.txt": "[학습 플랫폼] {couponName} 쿠폰이 발급되었습니다.",
        "WISHLIST_COURSE_DISCOUNT.txt": "[학습 플랫폼] 찜하신 {courseName} 강의가 할인 중입니다!",
        "COUPON_EXPIRY_REMINDER.txt": "[학습 플랫폼] 보유하신 {couponName} 쿠폰이 곧 만료됩니다."
    },
    "in_app": {
        "ENROLLMENT_COMPLETED.txt": "수강 신청 완료! {courseName} 강의를 확인해보세요.",
        "PAYMENT_CONFIRMED.txt": "결제 확정! {amount}원 결제가 완료되었습니다.",
        "COURSE_START_REMINDER.txt": "강의 시작 D-1! {courseName} 준비되셨나요?",
        "CANCELLATION_PROCESSED.txt": "취소 처리 완료: {courseName}",
        "NEW_LECTURE_OPENED.txt": "새 강의 오픈! {courseName}에서 확인해보세요.",
        "COUPON_ISSUED.txt": "쿠폰 발급 완료! {couponName}을(를) 사용해보세요.",
        "WISHLIST_COURSE_DISCOUNT.txt": "찜한 강의 할인! {courseName}을(를) 저렴하게 수강하세요.",
        "COUPON_EXPIRY_REMINDER.txt": "쿠폰 만료 임박! {couponName} 기회를 놓치지 마세요."
    }
}

for module, files in templates.items():
    base_dir = f"infrastructure/notifier/{module}/src/main/resources/templates"
    os.makedirs(base_dir, exist_ok=True)
    for filename, content in files.items():
        with open(os.path.join(base_dir, filename), "w", encoding="utf-8") as f:
            f.write(content)
