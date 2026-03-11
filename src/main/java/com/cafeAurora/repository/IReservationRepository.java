package com.cafeAurora.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.model.Reservation;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Integer> {
	@Query("SELECT r FROM Reservation r WHERE r.user.idUser = :idUser AND r.status IN ('PENDIENTE', 'CONFIRMADA')")
	List<Reservation> findActiveReservations(UUID idUser);

	@Query("SELECT r FROM Reservation r WHERE r.user.idUser = :idUser AND r.status IN ('RECHAZADA', 'CANCELADA', 'COMPLETADA', 'NO_SHOW')")
	List<Reservation> findHistoryReservations(UUID idUser);

	@Query("""
			SELECT r FROM Reservation r
			WHERE r.status = 'CONFIRMADA'
			AND r.reservationDate = :today
			ORDER BY r.reservationTime ASC
			""")
	List<Reservation> getTodayConfirmedReservations(@Param("today") LocalDate today);

	List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);

	List<Reservation> findByStatusAndAttendedByIdUser(ReservationStatus status, UUID idUser);

	@Query("""
			SELECT r FROM Reservation r
			WHERE r.status = 'CONFIRMADA'
			AND r.reservationDate = :today
			AND r.reservationTime >= :now
			ORDER BY r.reservationTime ASC
			""")
	List<Reservation> getNextReservations(LocalDate today, LocalTime now, Pageable pageable);

	@Query("""
			SELECT r.reservationDate, COUNT(r)
			FROM Reservation r
			WHERE r.reservationDate >= :startDate
			GROUP BY r.reservationDate
			ORDER BY r.reservationDate
			""")
	List<Object[]> getReservationsLastDays(@Param("startDate") LocalDate startDate);

	Long countByUser_IdUser(UUID idUser);

	Long countByStatus(ReservationStatus status);

	Long countByStatusAndReservationDate(ReservationStatus status, LocalDate reservationDate);

	Long countByReservationDate(LocalDate reservationDate);

	@Query("""
			SELECT COUNT(r)
			FROM Reservation r
			WHERE MONTH(r.reservationDate) = :month
			AND YEAR(r.reservationDate) = :year
			""")
	Long countReservationsByMonth(@Param("month") int month, @Param("year") int year);

	Optional<Reservation> findByIdReservationAndUser_IdUser(Integer idReservation, UUID idUser);
}
