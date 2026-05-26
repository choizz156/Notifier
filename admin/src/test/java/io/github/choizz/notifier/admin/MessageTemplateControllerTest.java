package io.github.choizz.notifier.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.choizz.notifier.admin.application.AdminMessageTemplateService;
import io.github.choizz.notifier.core.application.port.in.MessageTemplateUseCase;
import io.github.choizz.notifier.core.domain.model.Channel;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import io.github.choizz.notifier.core.domain.model.NotificationType;

@ExtendWith(MockitoExtension.class)
class MessageTemplateControllerTest {

	private MockMvc mockMvc;

	@Mock
	private AdminMessageTemplateService adminMessageTemplateService;

	@Mock
	private MessageTemplateUseCase messageTemplateUseCase;

	@InjectMocks
	private MessageTemplateController messageTemplateController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(messageTemplateController)
			.build();
	}

	@DisplayName("템플릿을 성공적으로 생성한다.")
	@Test
	void test1() throws Exception {
		// given
		MessageTemplate template = MessageTemplate.create(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, "결제완료 템플릿");
		when(adminMessageTemplateService.create(any(), any(), any())).thenReturn(template);

		String request = """
			{
				"channel": "IN_APP",
				"type": "PAYMENT_CONFIRMED",
				"content": "결제완료 템플릿"
			}
			""";

		// when & then
		mockMvc.perform(post("/v1/admin/templates")
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("결제완료 템플릿"))
			.andExpect(jsonPath("$.channel").value("IN_APP"))
			.andExpect(jsonPath("$.type").value("PAYMENT_CONFIRMED"));
	}

	@DisplayName("템플릿 내용을 성공적으로 수정한다.")
	@Test
	void test2() throws Exception {
		// given
		MessageTemplate template = MessageTemplate.create(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, "수정된 템플릿");
		when(adminMessageTemplateService.updateContent(eq(1L), any())).thenReturn(template);

		String request = """
			{
				"content": "수정된 템플릿"
			}
			""";

		// when & then
		mockMvc.perform(put("/v1/admin/templates/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("수정된 템플릿"));
	}

	@DisplayName("특정 템플릿을 조회한다.")
	@Test
	void test3() throws Exception {
		// given
		MessageTemplate template = MessageTemplate.create(Channel.IN_APP, NotificationType.PAYMENT_CONFIRMED, "조회 템플릿");
		when(messageTemplateUseCase.findById(1L)).thenReturn(template);

		// when & then
		mockMvc.perform(get("/v1/admin/templates/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("조회 템플릿"));
	}
}
