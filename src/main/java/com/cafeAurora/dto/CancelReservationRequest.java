package com.cafeAurora.dto;

import lombok.Data;

@Data
public class CancelReservationRequest {
	private Integer idReservation;
	private String notes;
}
