package io.github.choizz.notifier.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.github.choizz.notifier.core.domain.model.NotificationType;

public record ReservationCreateWebRequest(
	List<Long> subscriberIds,
	NotificationType type,
	LocalDateTime reservationTime
) {
}
