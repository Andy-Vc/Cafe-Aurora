package com.cafeAurora.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class RejectReservationRequest {
	private Integer idReservation;
    private UUID idRecepcionista;
    private String notes;
}
