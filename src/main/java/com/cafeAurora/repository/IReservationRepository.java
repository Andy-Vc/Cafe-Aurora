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

import com.cafeAurora.dto.ReportReceptionistDTO;
import com.cafeAurora.dto.ReportReservationDTO;
import com.cafeAurora.dto.ReportTableCoffeDTO;
import com.cafeAurora.enums.ReservationSource;
import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.model.TableCoffe;

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
			    SELECT new com.cafeAurora.dto.ReportReservationDTO(
			        r.idReservation, r.customerName, r.customerEmail, r.customerPhone,
			        r.reservationDate, r.reservationTime, r.numPeople,
			        r.status, r.source, t.tableNumber, t.location, r.specialNotes
			    )
			    FROM Reservation r
			    LEFT JOIN r.table t
			    WHERE r.reservationDate BETWEEN :start AND :end
			      AND (:status IS NULL OR r.status = :status)
			      AND (:source IS NULL OR r.source = :source)
			    ORDER BY r.reservationDate, r.reservationTime
			""")
	List<ReportReservationDTO> findReservationsByRange(@Param("start") LocalDate start, @Param("end") LocalDate end,
			 @Param("status") ReservationStatus status,   
			    @Param("source") ReservationSource source );

	@Query("""
			    SELECT new com.cafeAurora.dto.ReportTableCoffeDTO(
			        t.tableNumber, t.location, t.capacity,
			        COUNT(r),
			        SUM(CASE WHEN r.status = 'CONFIRMADA'  THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.status = 'CANCELADA'   THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.status = 'NO_SHOW'     THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.status = 'COMPLETADA'  THEN 1 ELSE 0 END)
			    )
			    FROM TableCoffe t
			    LEFT JOIN t.reservations r
			        ON r.reservationDate BETWEEN :start AND :end
			    GROUP BY t.tableNumber, t.location, t.capacity
			    ORDER BY t.tableNumber
			""")
	List<ReportTableCoffeDTO> findTableOccupationByRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("""
			    SELECT new com.cafeAurora.dto.ReportReceptionistDTO(
			        u.name, u.email,
			        COUNT(r),
			        SUM(CASE WHEN r.status = 'CONFIRMADA'  THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.status = 'RECHAZADA'   THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.status = 'COMPLETADA'  THEN 1 ELSE 0 END)
			    )
			    FROM Reservation r
			    JOIN r.attendedBy u
			    WHERE r.reservationDate BETWEEN :start AND :end
			    GROUP BY u.name, u.email
			    ORDER BY COUNT(r) DESC
			""")
	List<ReportReceptionistDTO> findReceptionistPerformanceByRange(@Param("start") LocalDate start,
			@Param("end") LocalDate end);

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

	boolean existsByTableAndReservationDateAndReservationTimeAndStatusIn(TableCoffe table, LocalDate reservationDate,
			LocalTime reservationTime, List<ReservationStatus> statuses);
}
