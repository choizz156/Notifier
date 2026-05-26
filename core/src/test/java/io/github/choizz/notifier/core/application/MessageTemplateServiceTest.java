package io.github.choizz.notifier.core.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.application.port.out.MessageTemplatePersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.NotificationType;

class MessageTemplateServiceTest {

	private final MessageTemplatePersistencePort persistencePort = mock(MessageTemplatePersistencePort.class);
	private final MessageTemplateService messageTemplateService = new MessageTemplateService(persistencePort);

	@DisplayName("활성화된 템플릿만 조회한다.")
	@Test
	void test1() {
		// given
		MessageTemplate activeTemplate = MessageTemplate.create(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, "활성");
		when(persistencePort.findByChannelAndNotificationType(any(), any()))
			.thenReturn(Optional.of(activeTemplate));

		// when
		Optional<MessageTemplate> result = messageTemplateService.findActiveTemplate(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().content()).isEqualTo("활성");
	}

	@DisplayName("비활성화된 템플릿은 조회되지 않는다.")
	@Test
	void test2() {
		// given
		when(persistencePort.findByChannelAndNotificationType(any(), any()))
			.thenReturn(Optional.empty());

		// when
		Optional<MessageTemplate> result = messageTemplateService.findActiveTemplate(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED);

		// then
		assertThat(result).isEmpty();
	}
}
