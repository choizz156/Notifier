package io.github.choizz.notifier.api.dto;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.choizz.notifier.core.domain.model.NotificationType;

public record PublicReservationCreateWebRequest(
	NotificationType type,
	Map<String, String> metadata,
	LocalDateTime reservationTime
) {
}
