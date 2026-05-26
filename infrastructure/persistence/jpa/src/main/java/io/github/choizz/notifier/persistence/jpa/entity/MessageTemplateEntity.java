package io.github.choizz.notifier.persistence.jpa.entity;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(fluent = true)
@Table(
	name = "message_templates",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_msg_tpl_channel_type",
			columnNames = {"channel", "notification_type"}
		)
	}
)
@Entity
public class MessageTemplateEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Channel channel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Column(columnDefinition = "text", nullable = false)
	private String content;

	@Column(nullable = false)
	private boolean isActive;

	@Builder
	public MessageTemplateEntity(
		Channel channel,
		NotificationType notificationType,
		String content,
		boolean isActive
	) {
		this.channel = channel;
		this.notificationType = notificationType;
		this.content = content;
		this.isActive = isActive;
	}
}
