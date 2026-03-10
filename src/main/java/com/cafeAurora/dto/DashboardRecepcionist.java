package com.cafeAurora.dto;

import java.util.List;

import com.cafeAurora.model.Reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRecepcionist {
	private Long totalReservations;
	private Long confirmedToday;
	private Long pending;
	private Long completedToday;
	private Long availableTables;
	private List<Reservation> nextReservations;
}