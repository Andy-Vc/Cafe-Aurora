package com.cafeAurora.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FilterReportDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String status;       // opcional
    private String source;       // opcional
    private Integer tableNumber; // opcional
}