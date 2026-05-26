package io.github.choizz.notifier.core.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Channel(@JsonValue String name) {
	
	public static final Channel EMAIL = new Channel("EMAIL");
	public static final Channel IN_APP = new Channel("IN_APP");

	@JsonCreator
	public static Channel of(String name) {
		return name != null ? new Channel(name.toUpperCase()) : null;
	}

	@Override
	public String toString() {
		return name;
	}
}
