package io.github.choizz.notifier.persistence.jpa.converter;

import io.github.choizz.notifier.core.domain.model.Channel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChannelConverter implements AttributeConverter<Channel, String> {

	@Override
	public String convertToDatabaseColumn(Channel attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.name();
	}

	@Override
	public Channel convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}
		return Channel.of(dbData);
	}
}
