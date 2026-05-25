package io.github.choizz.notifier.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.choizz.notifier.core.application.dto.PublicationContext;
import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.NotificationType;

class AbstractNotifierAdapterTest {

	static class TestNotifierAdapter extends AbstractNotifierAdapter {

		private Long lastSubscriberId;
		private String lastContent;

		public TestNotifierAdapter(TemplateRendererPort templateRendererPort) {
			super(templateRendererPort);
		}

		@Override
		protected String getChannelName() {
			return "IN_APP";
		}

		@Override
		protected void doSend(Long subscriberId, String content) {
			this.lastSubscriberId = subscriberId;
			this.lastContent = content;
		}

		public Long getLastSubscriberId() {
			return lastSubscriberId;
		}

		public String getLastContent() {
			return lastContent;
		}
	}

	@DisplayName("템플릿을 렌더링하고 doSend를 호출한다.")
	@Test
	void test1() {
		// given
		TemplateRendererPort templateRendererPort = mock(TemplateRendererPort.class);
		TestNotifierAdapter adapter = new TestNotifierAdapter(templateRendererPort);

		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.subscriberId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.IN_APP.name())
			.metadata("{\"key\":\"value\"}")
			.retryCount(0)
			.build();

		when(templateRendererPort.render(eq(Channel.IN_APP), eq(NotificationType.PAYMENT_CONFIRMED), anyMap()))
			.thenReturn("Rendered Content");

		// when
		adapter.publish(context);

		// then
		assertThat(adapter.getLastSubscriberId()).isEqualTo(100L);
		assertThat(adapter.getLastContent()).isEqualTo("Rendered Content");
	}

	@DisplayName("템플릿 렌더링 중 예외가 발생하면 IllegalStateException을 던진다.")
	@Test
	void test2() {
		// given
		TemplateRendererPort templateRendererPort = mock(TemplateRendererPort.class);
		TestNotifierAdapter adapter = new TestNotifierAdapter(templateRendererPort);

		PublicationContext context = PublicationContext.builder()
			.notificationId(1L)
			.subscriberId(100L)
			.notificationType(NotificationType.PAYMENT_CONFIRMED.name())
			.channel(Channel.IN_APP.name())
			.metadata("{\"key\":\"value\"}")
			.retryCount(0)
			.build();

		when(templateRendererPort.render(any(), any(), anyMap()))
			.thenThrow(new RuntimeException("Template error"));

		// when & then
		assertThatThrownBy(() -> adapter.publish(context))
			.isInstanceOf(IllegalStateException.class)
			.hasCauseInstanceOf(RuntimeException.class);
	}

	@DisplayName("채널 이름을 기반으로 지원 여부를 확인한다.")
	@Test
	void test3() {
		// given
		TemplateRendererPort templateRendererPort = mock(TemplateRendererPort.class);
		TestNotifierAdapter adapter = new TestNotifierAdapter(templateRendererPort);

		// when & then
		assertThat(adapter.supports("IN_APP")).isTrue();
		assertThat(adapter.supports("in_app")).isTrue();
		assertThat(adapter.supports("EMAIL")).isFalse();
	}
}
