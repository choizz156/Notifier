package io.github.choizz.notifier.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;

class TemplateRendererAdapterTest {

	private final TemplateRendererAdapter adapter = new TemplateRendererAdapter();

	@DisplayName("템플릿을 읽고 변수를 치환하여 반환한다.")
	@Test
	void test1() {
		// given
		Map<String, String> metadata = Map.of("key", "value");

		// when
		String result = adapter.render(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, metadata);

		// then
		assertThat(result.trim()).isEqualTo("Test Template value");
	}

	@DisplayName("존재하지 않는 템플릿의 경우 IllegalArgumentException이 발생한다.")
	@Test
	void test2() {
		// given
		Map<String, String> metadata = Map.of();

		// when & then
		assertThatThrownBy(() -> adapter.render(Channel.IN_APP, NotificationType.COUPON_ISSUED, metadata))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
