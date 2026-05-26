package io.github.choizz.notifier.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateEntity;

public interface MessageTemplateJpaRepository extends JpaRepository<MessageTemplateEntity, Long> {

	Optional<MessageTemplateEntity> findByChannelAndNotificationType(Channel channel, NotificationType notificationType);
}
