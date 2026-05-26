package io.github.choizz.notifier.persistence.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "public_notification_dlq")
@Entity
public class PublicNotificationDlqJpaEntity {

	public enum DlqStatus {
		PENDING, RESOLVED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "error_message", length = 2000)
	private String errorMessage;

	@Column(name = "event_payload", columnDefinition = "TEXT")
	private String eventPayload;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20, nullable = false)
	private DlqStatus status = DlqStatus.PENDING;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	private PublicNotificationDlqJpaEntity(Long id, String errorMessage, String eventPayload, DlqStatus status, LocalDateTime createdAt) {
		this.id = id;
		this.errorMessage = errorMessage;
		this.eventPayload = eventPayload;
		this.status = status != null ? status : DlqStatus.PENDING;
		this.createdAt = createdAt;
	}

	public void markAsResolved() {
		this.status = DlqStatus.RESOLVED;
	}
}
