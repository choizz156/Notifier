package io.github.choizz.notifier.core.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Channel(@JsonValue String name) { // "channel": "IN_APP" 형식
	
	public static final Channel EMAIL = new Channel("EMAIL");
	public static final Channel IN_APP = new Channel("IN_APP");

	@JsonCreator// 생성자 사용 유도
	public static Channel of(String name) {
		return name != null ? new Channel(name.toUpperCase()) : null;
	}
}
