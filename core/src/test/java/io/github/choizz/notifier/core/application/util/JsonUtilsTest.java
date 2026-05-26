package io.github.choizz.notifier.core.application.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {

	@DisplayName("Map을 JSON 문자열로 변환한다.")
	@Test
	void test1() {
		// given
		Map<String, String> map = Map.of("key1", "value1", "key2", "value2");

		// when
		String json = JsonUtils.toJson(map);

		// then
		assertThat(json).isNotNull();
		assertThat(json).contains("\"key1\":\"value1\"");
		assertThat(json).contains("\"key2\":\"value2\"");
	}

	@DisplayName("빈 Map이나 null을 JSON으로 변환하면 null을 반환한다.")
	@Test
	void test2() {
		assertThat(JsonUtils.toJson(null)).isNull();
		assertThat(JsonUtils.toJson(Map.of())).isNull();
	}

	@DisplayName("JSON 문자열을 Map으로 변환한다.")
	@Test
	void test3() {
		// given
		String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

		// when
		Map<String, String> map = JsonUtils.toMap(json);

		// then
		assertThat(map).hasSize(2)
			.containsEntry("key1", "value1")
			.containsEntry("key2", "value2");
	}

	@DisplayName("빈 문자열이나 null을 Map으로 변환하면 빈 Map을 반환한다.")
	@Test
	void test4() {
		assertThat(JsonUtils.toMap(null)).isEmpty();
		assertThat(JsonUtils.toMap("")).isEmpty();
	}

	@DisplayName("잘못된 JSON 문자열을 변환하려 하면 IllegalStateException이 발생한다.")
	@Test
	void test5() {
		// given
		String invalidJson = "{invalidJson}";

		// when & then
		assertThatThrownBy(() -> JsonUtils.toMap(invalidJson))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("JSON을 map으로 변환하는데 실패했습니다.");
	}
}
