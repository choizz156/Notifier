package io.github.choizz.notifier.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.admin.application.AdminMessageTemplateRepository;
import io.github.choizz.notifier.core.application.port.out.MessageTemplatePersistencePort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;

class AdminMessageTemplateServiceTest {

	private final AdminMessageTemplateRepository adminMessageTemplateRepository = mock(AdminMessageTemplateRepository.class);
	private final MessageTemplatePersistencePort messageTemplatePersistencePort = mock(MessageTemplatePersistencePort.class);
	private final AdminMessageTemplateService adminMessageTemplateService = new AdminMessageTemplateService(adminMessageTemplateRepository, messageTemplatePersistencePort);

	@DisplayName("템플릿을 신규 생성하고 스냅샷 이력을 저장한다.")
	@Test
	void test1() {
		// given
		Channel channel = Channel.IN_APP;
		NotificationType type = NotificationType.PAYMENT_CONFIRMED;
		String content = "결제 완료";

		when(messageTemplatePersistencePort.findByChannelAndNotificationType(channel, type))
			.thenReturn(Optional.empty());
		
		MessageTemplate template = MessageTemplate.create(channel, type, content);
		when(adminMessageTemplateRepository.save(any())).thenReturn(template);

		// when
		MessageTemplate result = adminMessageTemplateService.create(channel, type, content);

		// then
		assertThat(result.content()).isEqualTo(content);
		verify(adminMessageTemplateRepository, times(1)).save(any(MessageTemplate.class));
		verify(adminMessageTemplateRepository, times(1)).saveHistory(any(MessageTemplateHistory.class));
	}

	@DisplayName("이미 동일한 채널과 타입의 템플릿이 존재하면 예외가 발생한다.")
	@Test
	void test2() {
		// given
		Channel channel = Channel.IN_APP;
		NotificationType type = NotificationType.PAYMENT_CONFIRMED;

		when(messageTemplatePersistencePort.findByChannelAndNotificationType(channel, type))
			.thenReturn(Optional.of(MessageTemplate.create(channel, type, "기존 템플릿")));

		// when & then
		assertThatThrownBy(() -> adminMessageTemplateService.create(channel, type, "새 템플릿"))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("이미 해당 채널과 알림 타입에 대한 템플릿이 존재합니다.");
	}

	@DisplayName("템플릿의 내용을 수정하면 새로운 스냅샷 이력이 저장된다.")
	@Test
	void test3() {
		// given
		MessageTemplate existing = MessageTemplate.create(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, "기존");
		when(messageTemplatePersistencePort.findById(1L)).thenReturn(existing);
		when(adminMessageTemplateRepository.save(any())).thenReturn(existing);

		// when
		MessageTemplate result = adminMessageTemplateService.updateContent(1L, "수정됨");

		// then
		assertThat(result.content()).isEqualTo("수정됨");
		verify(adminMessageTemplateRepository, times(1)).save(existing);
		verify(adminMessageTemplateRepository, times(1)).saveHistory(any(MessageTemplateHistory.class));
	}
}
