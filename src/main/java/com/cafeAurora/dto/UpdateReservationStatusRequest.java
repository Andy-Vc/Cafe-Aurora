package com.cafeAurora.dto;

import java.util.UUID;

import com.cafeAurora.enums.ReservationStatus;

import lombok.Data;

@Data
public class UpdateReservationStatusRequest {
	private Integer idReservation;
    private UUID idRecepcionista;
    private ReservationStatus status;
}
