package com.cafeAurora.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cafeAurora.dto.DashboardAdmin;
import com.cafeAurora.dto.DashboardRecepcionist;
import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.repository.IReservationRepository;
import com.cafeAurora.repository.ITableCoffeRepository;
import com.cafeAurora.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	private final IReservationRepository reservationRepository;
	private final ITableCoffeRepository tableCoffeRepository;
    private final IUserRepository userRepository;

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
		Long availableTables = tableCoffeRepository.countByIsAvailableTrue();

		List<Reservation> nextReservations = reservationRepository.getNextReservations(today, now, limit);
		DashboardRecepcionist dashboard = new DashboardRecepcionist(todayReservations, confirmedToday, pending,
				completedToday, availableTables, nextReservations);
		return dashboard;
	}
	
	/* ADMIN */
	public DashboardAdmin getAdminDashboard(){
        LocalDate today = LocalDate.now();

        int month = today.getMonthValue();
        int year = today.getYear();

        LocalDate last7Days = today.minusDays(7);

        Long reservationsToday =
                reservationRepository.countByReservationDate(today);

        Long reservationsMonth =
                reservationRepository.countReservationsByMonth(month, year);

        Long pending =
                reservationRepository.countByStatus(ReservationStatus.PENDIENTE);

        Long confirmed =
                reservationRepository.countByStatus(ReservationStatus.CONFIRMADA);

        Long completed =
                reservationRepository.countByStatus(ReservationStatus.COMPLETADA);

        Long cancelled =
                reservationRepository.countByStatus(ReservationStatus.CANCELADA);
        
        Long noShow =
        		reservationRepository.countByStatus(ReservationStatus.NO_ASISTIO);
        
        Long rejectd =
        		reservationRepository.countByStatus(ReservationStatus.RECHAZADA);

        Long clients =
                userRepository.countByRole_IdRole(3);

        Long tables =
                tableCoffeRepository.count();

        List<Object[]> lastDays =
                reservationRepository.getReservationsLastDays(last7Days);

        return new DashboardAdmin(
                reservationsToday,
                reservationsMonth,
                pending,
                confirmed,
                completed,
                cancelled,
                rejectd,
                noShow,
                clients,
                tables,
                lastDays
        );
    }
}
