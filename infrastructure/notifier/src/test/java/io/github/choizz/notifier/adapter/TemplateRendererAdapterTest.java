package io.github.choizz.notifier.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import java.util.Optional;

import io.github.choizz.notifier.core.application.port.in.MessageTemplateUseCase;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;

class TemplateRendererAdapterTest {

	private final MessageTemplateUseCase messageTemplateUseCase = mock(MessageTemplateUseCase.class);
	private final TemplateRendererAdapter adapter = new TemplateRendererAdapter(messageTemplateUseCase);

	@DisplayName("DB에 활성화된 템플릿이 있으면 이를 사용하여 변수를 치환한다.")
	@Test
	void test1() {
		// given
		Map<String, String> metadata = Map.of("name", "홍길동");
		MessageTemplate dbTemplate = MessageTemplate.create(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, "안녕하세요 {name}님, 결제가 완료되었습니다.");

		when(messageTemplateUseCase.findActiveTemplate(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED))
			.thenReturn(Optional.of(dbTemplate));

		// when
		String result = adapter.render(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, metadata);

		// then
		assertThat(result).isEqualTo("안녕하세요 홍길동님, 결제가 완료되었습니다.");
	}

	@DisplayName("DB에 템플릿이 없으면 정적 파일을 읽어 치환한다.")
	@Test
	void test2() {
		// given
		Map<String, String> metadata = Map.of("key", "value");

		when(messageTemplateUseCase.findActiveTemplate(any(), any())).thenReturn(Optional.empty());

		// when
		String result = adapter.render(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, metadata);

		// then
		assertThat(result.trim()).isEqualTo("Test Template value");
	}

	@DisplayName("DB 조회 중 예외가 발생하면 폴백으로 정적 파일을 사용한다.")
	@Test
	void test3() {
		// given
		Map<String, String> metadata = Map.of("key", "fallback");

		when(messageTemplateUseCase.findActiveTemplate(any(), any())).thenThrow(new RuntimeException("DB 에러"));

		// when
		String result = adapter.render(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, metadata);

		// then
		assertThat(result.trim()).isEqualTo("Test Template fallback");
	}

	@DisplayName("존재하지 않는 템플릿의 경우 IllegalArgumentException이 발생한다.")
	@Test
	void test4() {
		// given
		Map<String, String> metadata = Map.of();

		when(messageTemplateUseCase.findActiveTemplate(any(), any())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adapter.render(Channel.IN_APP, NotificationType.COUPON_ISSUED, metadata))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
