package com.cafeAurora.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.model.Reservation;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Integer> {
	Optional<Reservation> findByIdReservationAndUser_IdUser(Integer idReservation, UUID idUser);

	@Query("SELECT r FROM Reservation r WHERE r.user.idUser = :idUser AND r.status IN ('PENDIENTE', 'CONFIRMADA')")
	List<Reservation> findActiveReservations(UUID idUser);

	@Query("SELECT r FROM Reservation r WHERE r.user.idUser = :idUser AND r.status IN ('RECHAZADA', 'CANCELADA', 'COMPLETADA')")
	List<Reservation> findHistoryReservations(UUID idUser);

	long countByUser_IdUser(UUID idUser);

	List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);

	List<Reservation> findByStatusAndAttendedByIdUser(ReservationStatus status, UUID idUser);
}
