package io.github.choizz.notifier.api.dto;

import java.util.Map;

public record PublicNotificationWebRequest(
	String type,
	Map<String, String> metadata
) {

}
