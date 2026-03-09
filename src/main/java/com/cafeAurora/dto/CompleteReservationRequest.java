package com.cafeAurora.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CompleteReservationRequest {
	private Integer idReservation;
    private UUID idRecepcionista;
}
