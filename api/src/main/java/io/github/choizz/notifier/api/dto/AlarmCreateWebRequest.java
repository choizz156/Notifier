package io.github.choizz.notifier.api.dto;

import java.util.Map;

public record AlarmCreateWebRequest(
	Long subscriberId,
	String alarmType,
	String channel,
	Map<String, String> metadata
) {

}
