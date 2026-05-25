package io.github.choizz.notifier.inapp.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.choizz.notifier.core.application.port.out.TemplateRendererPort;
import io.github.choizz.notifier.core.domain.model.Channel;

@ExtendWith(MockitoExtension.class)
class InMemoryInAppNotifierAdapterTest {

	@Mock
	private TemplateRendererPort templateRendererPort;

	@InjectMocks
	private InMemoryInAppNotifierAdapter adapter;

	@DisplayName("인앱 알림 채널 이름을 반환한다.")
	@Test
	void test1() {
		assertThat(adapter.supports(Channel.IN_APP.name())).isTrue();
	}

	@DisplayName("doSend 메서드가 예외 없이 실행된다.")
	@Test
	void test2() {
		assertThatCode(() -> adapter.doSend(100L, "test content"))
			.doesNotThrowAnyException();
	}
}
