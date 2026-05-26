package io.github.choizz.notifier.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.admin.dto.MessageTemplateCreateRequest;
import io.github.choizz.notifier.admin.dto.MessageTemplateHistoryResponse;
import io.github.choizz.notifier.admin.dto.MessageTemplateResponse;
import io.github.choizz.notifier.admin.dto.MessageTemplateUpdateRequest;
import io.github.choizz.notifier.admin.application.AdminMessageTemplateService;
import io.github.choizz.notifier.core.application.port.in.MessageTemplateUseCase;
import io.github.choizz.notifier.core.domain.model.MessageTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/admin/templates")
@RestController
public class MessageTemplateController {

	private final AdminMessageTemplateService adminMessageTemplateService;
	private final MessageTemplateUseCase messageTemplateUseCase;

	@PostMapping
	public MessageTemplateResponse create(@RequestBody MessageTemplateCreateRequest request) {
		MessageTemplate template = adminMessageTemplateService.create(request.channel(), request.type(), request.content());
		return MessageTemplateResponse.from(template);
	}

	@PutMapping("/{id}")
	public MessageTemplateResponse updateContent(@PathVariable Long id, @RequestBody MessageTemplateUpdateRequest request) {
		MessageTemplate template = adminMessageTemplateService.updateContent(id, request.content());
		return MessageTemplateResponse.from(template);
	}

	@GetMapping
	public List<MessageTemplateResponse> findAll() {
		return messageTemplateUseCase.findAll().stream()
			.map(MessageTemplateResponse::from)
			.toList();
	}

	@GetMapping("/{id}")
	public MessageTemplateResponse findById(@PathVariable Long id) {
		MessageTemplate template = messageTemplateUseCase.findById(id);
		return MessageTemplateResponse.from(template);
	}

	@GetMapping("/{id}/histories")
	public List<MessageTemplateHistoryResponse> findHistories(@PathVariable Long id) {
		return messageTemplateUseCase.findHistories(id).stream()
			.map(MessageTemplateHistoryResponse::from)
			.toList();
	}
}
