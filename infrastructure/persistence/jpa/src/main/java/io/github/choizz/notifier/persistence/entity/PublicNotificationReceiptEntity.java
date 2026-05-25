package io.github.choizz.notifier.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@Table(
	name = "public_notification_receipts",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_public_notification_receipt_duplicate",
			columnNames = {"subscriber_id", "public_notification_id"}
		)
	}
)
@Entity
public class PublicNotificationReceiptEntity extends BaseEntity {

	@Column(nullable = false)
	private Long subscriberId;

	@Column(nullable = false)
	private Long publicNotificationId;

	@Builder
	public PublicNotificationReceiptEntity(Long subscriberId, Long publicNotificationId) {
		this.subscriberId = subscriberId;
		this.publicNotificationId = publicNotificationId;
	}
}
