package io.github.choizz.notifier.persistence.jpa.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(fluent = true)
@Table(name = "message_template_histories")
@Entity
public class MessageTemplateHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long templateId;

	@Column(columnDefinition = "text", nullable = false)
	private String content;

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Builder
	public MessageTemplateHistoryEntity(Long templateId, String content) {
		this.templateId = templateId;
		this.content = content;
	}
	
	public void id(Long id) {
		this.id = id;
	}
}
