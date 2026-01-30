package com.cafeAurora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmReservationRequest {
    private Integer idReservation;
    private UUID idRecepcionista;
    private Integer idTable;
    private String notes;
}

