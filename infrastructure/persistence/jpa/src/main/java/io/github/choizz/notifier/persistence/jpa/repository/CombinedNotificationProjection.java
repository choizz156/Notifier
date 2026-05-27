package io.github.choizz.notifier.persistence.jpa.repository;

import java.time.LocalDateTime;

public interface CombinedNotificationProjection {
	Long getId();
	String getReference_type();
	Long getSubscriber_id();
	String getNotification_type();
	String getChannel();
	String getStatus();
	String getMessage();
	Boolean getIs_read();
	LocalDateTime getCreated_at();
	LocalDateTime getUpdated_at();
	Integer getRecover_count();
}
