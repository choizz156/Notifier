package io.github.choizz.notifier.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.choizz.notifier.api.dto.PublicReservationCreateWebRequest;
import io.github.choizz.notifier.core.application.port.in.ReservationUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/reservations")
public class ReservationController {

	private final ReservationUseCase reservationUseCase;


	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping("/public")
	public void reservePublic(@RequestBody PublicReservationCreateWebRequest request) {
		reservationUseCase.reservePublic(request.type(), request.metadata(), request.reservationTime());
	}
}
