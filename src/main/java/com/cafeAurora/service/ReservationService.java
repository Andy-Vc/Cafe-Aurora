package com.cafeAurora.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cafeAurora.dto.ReservationStatus;
import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.dto.TableStatus;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.model.TableCoffe;
import com.cafeAurora.model.User;
import com.cafeAurora.repository.IReservationRepository;
import com.cafeAurora.repository.ITableCoffeRepository;
import com.cafeAurora.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final IReservationRepository reservationRepository;
	private final ITableCoffeRepository tableCoffeRepository;
	private final IUserRepository userRepository;
	
	/* CUSTOMER */
	public ResultResponse createReservation(Reservation reservation) {
		try {
			String message = "¡Reserva registrada con éxito! Nos pondremos en contacto contigo pronto para confirmar los detalles.";
			reservationRepository.save(reservation);
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public List<Reservation> getActiveReservationsForUser(UUID idUser) {
		return reservationRepository.findActiveReservations(idUser);
	}

	public List<Reservation> getHistoryForUser(UUID idUser) {
		return reservationRepository.findHistoryReservations(idUser);
	}

	public Reservation getReservationForUserById(Integer idReservation, UUID idUser) {
		return reservationRepository.findByIdReservationAndUser_IdUser(idReservation, idUser).orElse(null);
	}

	public long countReservationsForUser(UUID idUser) {
		return reservationRepository.countByUser_IdUser(idUser);
	}

	/* RECEPCIONIST */
	public List<Reservation> getReservationPendiente() {
		return reservationRepository.findByStatusOrderByCreatedAtDesc(ReservationStatus.PENDIENTE);
	}

	public ResultResponse confirmReservation(Integer idReservation, UUID idRecepcionista, String notes,Integer idTable) {
		try {
			Reservation reservation = reservationRepository.findById(idReservation)
	                .orElseThrow(() -> new RuntimeException("La reserva no existe"));

	        TableCoffe table = tableCoffeRepository.findById(idTable)
	                .orElseThrow(() -> new RuntimeException("La mesa no existe"));

	        User recepcionista = userRepository.findById(idRecepcionista)
	                .orElseThrow(() -> new RuntimeException("El recepcionista no existe"));

	        reservation.setStatus(ReservationStatus.CONFIRMADA);
	        reservation.setAttendedBy(recepcionista);
	        reservation.setResponseNotes(notes);
	        reservation.setUpdatedAt(LocalDateTime.now());
	        reservation.setTable(table);  

	        table.setStatus(TableStatus.RESERVADA);

	        tableCoffeRepository.save(table);
	        reservationRepository.save(reservation);

	        return new ResultResponse(true, "Reserva confirmada con éxito");
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}
	
	public List<Reservation> getConfirmedByRecepcionist(UUID idRecepcionist){
		return reservationRepository.findByStatusAndAttendedByIdUser(ReservationStatus.CONFIRMADA, idRecepcionist);
	}
}
