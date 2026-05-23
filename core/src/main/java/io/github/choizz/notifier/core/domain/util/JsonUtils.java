package io.github.choizz.notifier.core.domain.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class JsonUtils {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static String toJson(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		try {
			return OBJECT_MAPPER.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("metadata를 JSON으로 변환하는데 실패했습니다.", e);
		}
	}

	public static Map<String, String> toMap(String json) {
		if (json == null || json.isEmpty()) {
			return Map.of();
		}
		try {
			return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, String>>() {});
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("JSON을 map으로 변환하는데 실패했습니다.", e);
		}
	}
}
