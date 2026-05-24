package io.github.choizz.notifier.core.application.support;

public interface PublishProcessor<T> {
	void process(T item);
}
