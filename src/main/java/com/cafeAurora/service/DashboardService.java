package com.cafeAurora.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cafeAurora.dto.DashboardRecepcionist;
import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.enums.TableStatus;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.repository.IReservationRepository;
import com.cafeAurora.repository.ITableCoffeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	private final IReservationRepository reservationRepository;
	private final ITableCoffeRepository tableCoffeRepository;

	/* RECEPCIONIST */
	public DashboardRecepcionist getReceptionistDashboard() {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		Pageable limit = PageRequest.of(0, 5);

		Long todayReservations = reservationRepository.countByReservationDate(today);
		Long confirmedToday = reservationRepository.countByStatusAndReservationDate(ReservationStatus.CONFIRMADA,
				today);
		Long pending = reservationRepository.countByStatus(ReservationStatus.PENDIENTE);
		Long completedToday = reservationRepository.countByStatusAndReservationDate(ReservationStatus.COMPLETADA,
				today);
		Long availableTables = tableCoffeRepository.countByStatus(TableStatus.DISPONIBLE);

		List<Reservation> nextReservations = reservationRepository.getNextReservations(today, now, limit);
		DashboardRecepcionist dashboard = new DashboardRecepcionist(todayReservations, confirmedToday, pending,
				completedToday, availableTables, nextReservations);
		return dashboard;
	}
}
