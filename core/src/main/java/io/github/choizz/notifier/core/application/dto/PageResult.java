package io.github.choizz.notifier.core.application.dto;

import java.util.List;

public record PageResult<T>(
	List<T> content,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
}
