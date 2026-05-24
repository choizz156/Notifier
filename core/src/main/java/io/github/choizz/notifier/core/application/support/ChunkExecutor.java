package io.github.choizz.notifier.core.application.support;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChunkExecutor {

	public static final int CHUNK_SIZE = 500;

	public static <T, ID> void execute(
		ID initId,
		Function<T, ID> idExtractor,
		Function<ID, List<T>> reader,
		Consumer<List<T>> processor
	) {

		ID lastId = initId;

		while (true) {
			List<T> chunk = reader.apply(lastId);

			if (chunk == null || chunk.isEmpty()) {
				break;
			}

			processor.accept(chunk);

			if (chunk.size() < CHUNK_SIZE) {
				break;
			}

			lastId = idExtractor.apply(chunk.getLast());
		}
	}
}
