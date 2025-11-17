package com.cafeAurora.service;

import org.springframework.stereotype.Service;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.repository.IReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final IReservationRepository reservationRepository;

	public ResultResponse createReservation(Reservation reservation) {
		try {
			String message = "¡Reserva registrada con éxito! Nos pondremos en contacto contigo pronto para confirmar los detalles.";
			reservationRepository.save(reservation);
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}
}
