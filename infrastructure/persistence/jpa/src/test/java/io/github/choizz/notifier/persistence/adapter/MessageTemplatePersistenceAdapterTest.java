package io.github.choizz.notifier.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.MessageTemplateHistory;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.jpa.adapter.MessageTemplatePersistenceAdapter;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateEntity;
import io.github.choizz.notifier.persistence.jpa.entity.MessageTemplateHistoryEntity;
import io.github.choizz.notifier.persistence.jpa.repository.MessageTemplateHistoryJpaRepository;
import io.github.choizz.notifier.persistence.jpa.repository.MessageTemplateJpaRepository;

@ExtendWith(MockitoExtension.class)
class MessageTemplatePersistenceAdapterTest {

	@Mock
	private MessageTemplateJpaRepository messageTemplateJpaRepository;

	@Mock
	private MessageTemplateHistoryJpaRepository messageTemplateHistoryJpaRepository;

	@InjectMocks
	private MessageTemplatePersistenceAdapter adapter;

	@DisplayName("템플릿을 저장하고 단건 조회한다.")
	@Test
	void test1() {
		// given
		MessageTemplate template = MessageTemplate.create(Channel.EMAIL, NotificationType.COUPON_ISSUED, "이메일 쿠폰 발급");
		MessageTemplateEntity entity = MessageTemplateEntity.builder()
			.channel(Channel.EMAIL)
			.notificationType(NotificationType.COUPON_ISSUED)
			.content("이메일 쿠폰 발급")
			.isActive(true)
			.build();
		entity.id(1L);

		when(messageTemplateJpaRepository.save(any(MessageTemplateEntity.class))).thenReturn(entity);
		when(messageTemplateJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

		MessageTemplate saved = adapter.save(template);

		// when
		MessageTemplate found = adapter.findById(saved.id());

		// then
		assertThat(found).isNotNull();
		assertThat(found.id()).isEqualTo(1L);
		assertThat(found.content()).isEqualTo("이메일 쿠폰 발급");
	}

	@DisplayName("채널과 타입으로 템플릿을 조회한다.")
	@Test
	void test2() {
		// given
		MessageTemplateEntity entity = MessageTemplateEntity.builder()
			.channel(Channel.IN_APP)
			.notificationType(NotificationType.NEW_LECTURE_OPENED)
			.content("새로운 강의 오픈")
			.isActive(true)
			.build();
		
		when(messageTemplateJpaRepository.findByChannelAndNotificationType(Channel.IN_APP, NotificationType.NEW_LECTURE_OPENED))
			.thenReturn(Optional.of(entity));

		// when
		Optional<MessageTemplate> found = adapter.findByChannelAndNotificationType(Channel.IN_APP, NotificationType.NEW_LECTURE_OPENED);

		// then
		assertThat(found).isPresent();
		assertThat(found.get().content()).isEqualTo("새로운 강의 오픈");
	}

	@DisplayName("템플릿 이력을 저장하고 조회한다.")
	@Test
	void test3() {
		// given
		MessageTemplateHistory history = MessageTemplateHistory.builder()
			.templateId(1L)
			.content("히스토리 내용")
			.build();
			
		MessageTemplateHistoryEntity entity = MessageTemplateHistoryEntity.builder()
			.templateId(1L)
			.content("히스토리 내용")
			.build();
		entity.id(100L);

		when(messageTemplateHistoryJpaRepository.save(any(MessageTemplateHistoryEntity.class))).thenReturn(entity);
		when(messageTemplateHistoryJpaRepository.findByTemplateIdOrderByCreatedAtDesc(1L))
			.thenReturn(List.of(entity));

		adapter.saveHistory(history);

		// when
		List<MessageTemplateHistory> histories = adapter.findHistoriesByTemplateId(1L);

		// then
		assertThat(histories).hasSize(1);
		assertThat(histories.get(0).content()).isEqualTo("히스토리 내용");
	}
}
