package com.cafeAurora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import com.cafeAurora.enums.ReservationSource;
import com.cafeAurora.enums.ReservationStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportReservationDTO {
    private Integer idReservation;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer numPeople;
    private ReservationStatus status;
    private ReservationSource source;
    private Integer tableNumber;
    private String tableLocation;
    private String specialNotes;
}
