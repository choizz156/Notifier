package io.github.choizz.logging;

import static net.logstash.logback.argument.StructuredArguments.*;

public class LogContent {

	public static Object[] exception(Exception e) {

		return new Object[] {
			kv("exception_class", e.getClass().getName()),
			kv("exception_message", e.getMessage()),
			e
		};
	}
}
