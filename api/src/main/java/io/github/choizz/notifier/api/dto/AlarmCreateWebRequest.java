package io.github.choizz.notifier.api.dto;

import io.github.choizz.notifier.application.dto.AlarmContext;
import java.util.Map;

public record AlarmCreateWebRequest(
    Long subscriberId,
    String alarmType,
    String channel,
    Map<String, String> metadata
) {
    public AlarmContext toContext() {
        return new AlarmContext(subscriberId, alarmType, channel, metadata);
    }
}
