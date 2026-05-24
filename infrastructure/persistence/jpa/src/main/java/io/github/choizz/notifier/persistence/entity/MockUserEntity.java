package io.github.choizz.notifier.persistence.entity;

import java.util.HashMap;
import java.util.Map;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mock_users")
@Entity
public class MockUserEntity extends BaseEntity {

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "mock_user_notification_settings", joinColumns = @JoinColumn(name = "mock_user_id"))
	@MapKeyEnumerated(EnumType.STRING)
	@Column(name = "is_subscribed", nullable = false)
	private Map<NotificationType, Boolean> notificationSettings = new HashMap<>();

	public MockUserEntity(Map<NotificationType, Boolean> notificationSettings) {
		this.notificationSettings = notificationSettings;
	}
}
