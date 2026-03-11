package com.cafeAurora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportTableCoffeDTO {
    private Integer tableNumber;
    private String location;
    private Integer capacity;
    private Long totalReservations;
    private Long confirmed;
    private Long cancelled;
    private Long noShow;
    private Long completed;
}