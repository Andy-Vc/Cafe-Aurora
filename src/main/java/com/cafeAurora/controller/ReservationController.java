package com.cafeAurora.controller;

import java.util.List;
import java.util.UUID;
import com.cafeAurora.dto.CancelReservationRequest;
import com.cafeAurora.dto.CompleteReservationRequest;
import com.cafeAurora.dto.ConfirmReservationRequest;
import com.cafeAurora.dto.RejectReservationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	/* RECEPCIONIST */
	@GetMapping("/list/pendientes")
	public ResponseEntity<?> getPendientes() {
		try {
			List<Reservation> pendientes = reservationService.getReservationPendiente();

			if (pendientes.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay reservas pendientes.");
			}

			return ResponseEntity.ok(pendientes);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener reservas pendientes.");
		}
	}

	@PutMapping("/confirm/{idReservation}")
	public ResponseEntity<ResultResponse> confirmReservation(
			@PathVariable Integer idReservation,
			@RequestBody ConfirmReservationRequest request
	) {
		try {
			request.setIdReservation(idReservation);

			ResultResponse result = reservationService.confirmReservation(request);

			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResultResponse(false, e.getMessage()));
		}
	}

	@PutMapping("/reject/{idReservation}")
	public ResponseEntity<ResultResponse> rejectdReservation(
	        @PathVariable Integer idReservation,
	        @RequestBody RejectReservationRequest request
	) {
	    try {
	        request.setIdReservation(idReservation);

	        ResultResponse result = reservationService.rejectReservation(request);

	        if (!result.getValue()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
	        }

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResultResponse(false, e.getMessage()));
	    }
	}
	
	@PutMapping("/complete/{idReservation}")
	public ResponseEntity<ResultResponse> completeReservation(
	        @PathVariable Integer idReservation,
	        @RequestBody CompleteReservationRequest request
	) {
	    try {
	        request.setIdReservation(idReservation);

	        ResultResponse result = reservationService.completeReservation(request);

	        if (!result.getValue()) {
	            return ResponseEntity.badRequest().body(result);
	        }

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResultResponse(false, e.getMessage()));
	    }
	}
	
	@GetMapping("/confirmed/today")
	public ResponseEntity<?> getTodayConfirmedReservations() {
	    try {
	        List<Reservation> confirmed = reservationService.getTodayConfirmedReservations();

	        if (confirmed.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT)
	                    .body("No hay reservas confirmadas hoy.");
	        }

	        return ResponseEntity.ok(confirmed);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al obtener reservas confirmadas.");
	    }
	}
	
	@GetMapping("/list/rejectd/{idRecepcionist}")
	public ResponseEntity<?> getRejectdByRecepcionist(@PathVariable UUID idRecepcionist) {
		try {
			List<Reservation> confirmed = reservationService.getRejectdByRecepcionist(idRecepcionist);

			if (confirmed.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("El recepcionista no tiene reservas rechazadas.");
			}

			return ResponseEntity.ok(confirmed);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener reservas rechazadas.");
		}
	}
	
	@GetMapping("/list/confirmed/{idRecepcionist}")
	public ResponseEntity<?> getConfirmedByRecepcionist(@PathVariable UUID idRecepcionist) {
		try {
			List<Reservation> confirmed = reservationService.getConfirmedByRecepcionist(idRecepcionist);

			if (confirmed.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("El recepcionista no tiene reservas confirmadas.");
			}

			return ResponseEntity.ok(confirmed);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener reservas confirmadas.");
		}
	}

	/* CUSTOMER */
	@PostMapping("/register")
	public ResponseEntity<ResultResponse> createReservation(@RequestBody Reservation reservation) {
		try {
			ResultResponse result = reservationService.createReservation(reservation);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	
	@PutMapping("/cancel")
	public ResponseEntity<ResultResponse> cancelReservation(@RequestBody CancelReservationRequest reservation) {
	    try {
	        ResultResponse result = reservationService.cancelReservation(reservation);

	        if (!result.getValue()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
	        }

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        e.printStackTrace();
	        ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	@GetMapping("/list/active/{idUser}")
	public ResponseEntity<?> getActiveReservations(@PathVariable UUID idUser) {
		try {
			List<Reservation> reservations = reservationService.getActiveReservationsForUser(idUser);

			if (reservations.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay reservas activas.");
			}

			return ResponseEntity.ok(reservations);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener reservas activas.");
		}
	}

	@GetMapping("/list/history/{idUser}")
	public ResponseEntity<?> getHistoryReservations(@PathVariable UUID idUser) {
		try {
			List<Reservation> reservations = reservationService.getHistoryForUser(idUser);

			if (reservations.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("El historial está vacío.");
			}

			return ResponseEntity.ok(reservations);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener historial.");
		}
	}

	@GetMapping("/count/{idUser}")
	public ResponseEntity<?> countReservationsByUser(@PathVariable UUID idUser) {
		try {
			long total = reservationService.countReservationsForUser(idUser);

			return ResponseEntity.ok(total);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al contar las reservas.");
		}
	}
}
