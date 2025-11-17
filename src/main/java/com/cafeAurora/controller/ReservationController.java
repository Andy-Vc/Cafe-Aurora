package com.cafeAurora.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
