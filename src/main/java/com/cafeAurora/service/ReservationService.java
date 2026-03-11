package com.cafeAurora.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.cafeAurora.dto.CancelReservationRequest;
import com.cafeAurora.dto.UpdateReservationStatusRequest;
import com.cafeAurora.dto.ConfirmReservationRequest;
import com.cafeAurora.dto.RejectReservationRequest;
import org.springframework.stereotype.Service;

import com.cafeAurora.enums.ReservationSource;
import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.model.TableCoffe;
import com.cafeAurora.model.User;
import com.cafeAurora.repository.IReservationRepository;
import com.cafeAurora.repository.ITableCoffeRepository;
import com.cafeAurora.repository.IUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final IReservationRepository reservationRepository;
	private final ITableCoffeRepository tableCoffeRepository;
	private final IUserRepository userRepository;
	private final EmailService emailService;

	/* CUSTOMER */
	public ResultResponse createReservation(Reservation reservation) {
		try {
			reservationRepository.save(reservation);

			String subject = "Confirmación de solicitud de reserva - Café Aurora";

			String message = "Hola " + reservation.getCustomerName() + ",\n\n"
					+ "¡Gracias por elegir Café Aurora! ☕\n\n"
					+ "Hemos recibido tu solicitud de reserva con los siguientes detalles:\n\n" + "📅 Fecha: "
					+ reservation.getReservationDate() + "\n" + "⏰ Hora: " + reservation.getReservationTime() + "\n"
					+ "👥 Número de personas: " + reservation.getNumPeople() + "\n\n"
					+ "Nuestro equipo de recepción revisará tu solicitud y te confirmará la disponibilidad lo antes posible.\n\n"
					+ "Si necesitas modificar tu reserva o tienes alguna consulta, no dudes en contactarnos.\n\n"
					+ "Te esperamos en Café Aurora.\n\n" + "Saludos,\n" + "Equipo de Café Aurora\n\n"
					+ "— Este es un mensaje automático, por favor no respondas a este correo —";

			emailService.sendEmail(reservation.getCustomerEmail(), subject, message);

			return new ResultResponse(true, "¡Reserva registrada con éxito! Te enviaremos la confirmación por correo.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse cancelReservation(CancelReservationRequest request) {
		try {
			Reservation reservation = reservationRepository.findById(request.getIdReservation())
					.orElseThrow(() -> new RuntimeException("La reserva no existe"));

			if (reservation.getStatus() == ReservationStatus.RECHAZADA
					|| reservation.getStatus() == ReservationStatus.COMPLETADA) {

				return new ResultResponse(false, "Esta reserva ya no puede cancelarse");
			}

			reservation.setStatus(ReservationStatus.CANCELADA);
			reservation.setResponseNotes(request.getNotes());
			reservation.setUpdatedAt(LocalDateTime.now());

			reservationRepository.save(reservation);

			return new ResultResponse(true, "Reserva cancelada con éxito");

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
	@Transactional
	public ResultResponse createReceptionReservation(Reservation request) {
		try {

			TableCoffe table = tableCoffeRepository.findById(request.getTable().getIdTable())
					.orElseThrow(() -> new RuntimeException("La mesa no existe"));

			User recepcionista = userRepository.findById(request.getAttendedBy().getIdUser())
					.orElseThrow(() -> new RuntimeException("El recepcionista no existe"));

			Reservation reservation = new Reservation();

			reservation.setReservationDate(request.getReservationDate());
			reservation.setReservationTime(request.getReservationTime());
			reservation.setNumPeople(request.getNumPeople());

			reservation.setCustomerName(request.getCustomerName());
			reservation.setCustomerPhone(request.getCustomerPhone());
			reservation.setCustomerEmail(request.getCustomerEmail());

			reservation.setSpecialNotes(request.getSpecialNotes());

			reservation.setStatus(ReservationStatus.CONFIRMADA);
			reservation.setSource(ReservationSource.RECEPCION);

			reservation.setTable(table);
			reservation.setAttendedBy(recepcionista);

			reservation.setCreatedAt(LocalDateTime.now());
			reservation.setUpdatedAt(LocalDateTime.now());

			tableCoffeRepository.save(table);
			reservationRepository.save(reservation);

			if (reservation.getCustomerEmail() != null && !reservation.getCustomerEmail().isBlank()) {

				String subject = "Reserva registrada - Café Aurora";

				String message = "Hola " + reservation.getCustomerName() + ",\n\n"
						+ "Tu reserva ha sido registrada en Café Aurora.\n\n" +

						"📅 Fecha: " + reservation.getReservationDate() + "\n" + "⏰ Hora: "
						+ reservation.getReservationTime() + "\n" + "🍽 Mesa: " + table.getTableNumber() + "\n\n" +

						"Te esperamos. ☕\n\n" + "Equipo Café Aurora";

				emailService.sendEmail(reservation.getCustomerEmail(), subject, message);
			}

			return new ResultResponse(true, "Reserva creada correctamente");

		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public List<Reservation> getReservationPendiente() {
		return reservationRepository.findByStatusOrderByCreatedAtDesc(ReservationStatus.PENDIENTE);
	}

	public ResultResponse confirmReservation(ConfirmReservationRequest request) {
		try {
			Reservation reservation = reservationRepository.findById(request.getIdReservation())
					.orElseThrow(() -> new RuntimeException("La reserva no existe"));

			if (reservation.getStatus() != ReservationStatus.PENDIENTE) {
				return new ResultResponse(false, "Solo se pueden confirmar reservas en estado PENDIENTE");
			}

			TableCoffe table = tableCoffeRepository.findById(request.getIdTable())
					.orElseThrow(() -> new RuntimeException("La mesa no existe"));

			boolean mesaOcupada = reservationRepository.existsByTableAndReservationDateAndReservationTimeAndStatusIn(
					table, reservation.getReservationDate(), reservation.getReservationTime(),
					List.of(ReservationStatus.CONFIRMADA, ReservationStatus.PENDIENTE));

			if (mesaOcupada) {
				return new ResultResponse(false, "La mesa ya está ocupada en esa fecha y hora");
			}

			User recepcionista = userRepository.findById(request.getIdRecepcionista())
					.orElseThrow(() -> new RuntimeException("El recepcionista no existe"));

			reservation.setStatus(ReservationStatus.CONFIRMADA);
			reservation.setAttendedBy(recepcionista);
			reservation.setResponseNotes(request.getNotes());
			reservation.setUpdatedAt(LocalDateTime.now());
			reservation.setTable(table);

			reservationRepository.save(reservation);
			String subject = "¡Tu reserva está confirmada! - Café Aurora";

			String message = "Hola " + reservation.getCustomerName() + ",\n\n" +

					"¡Buenas noticias! Tu reserva ha sido CONFIRMADA. ☕\n\n" +

					"📋 Detalles de tu reserva:\n" + "📅 Fecha: " + reservation.getReservationDate() + "\n" + "⏰ Hora: "
					+ reservation.getReservationTime() + "\n" + " 🍽 Mesa asignada: " + table.getTableNumber() + "\n"
					+ "👤 Recepcionista: " + recepcionista.getName() + "\n\n" +

					"Te esperamos en Café Aurora para brindarte una experiencia agradable.\n\n" +

					"Si necesitas realizar algún cambio en tu reserva, no dudes en contactarnos.\n\n" +

					"¡Nos vemos pronto! ✨\n\n" + "Equipo de Café Aurora\n\n"
					+ "— Este es un mensaje automático, por favor no respondas a este correo —";

			emailService.sendEmail(reservation.getCustomerEmail(), subject, message);
			return new ResultResponse(true, "Reserva confirmada con éxito");

		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse rejectReservation(RejectReservationRequest request) {
		try {
			Reservation reservation = reservationRepository.findById(request.getIdReservation())
					.orElseThrow(() -> new RuntimeException("La reserva no existe"));

			if (reservation.getStatus() != ReservationStatus.PENDIENTE) {
				return new ResultResponse(false, "Solo se pueden rechazar reservas en estado PENDIENTE");
			}

			User recepcionista = userRepository.findById(request.getIdRecepcionista())
					.orElseThrow(() -> new RuntimeException("El recepcionista no existe"));

			reservation.setStatus(ReservationStatus.RECHAZADA);
			reservation.setAttendedBy(recepcionista);
			reservation.setResponseNotes(request.getNotes());
			reservation.setUpdatedAt(LocalDateTime.now());

			reservationRepository.save(reservation);

			String subject = "Actualización sobre tu reserva - Café Aurora";

			String message = "Hola " + reservation.getCustomerName() + ",\n\n" +

					"Gracias por tu interés en reservar con Café Aurora.\n\n" +

					"Lamentablemente, en esta ocasión no hemos podido confirmar tu reserva.\n\n" +

					"📌 Motivo:\n" + request.getNotes() + "\n\n" +

					"Te invitamos a intentar nuevamente con otro horario o fecha.\n"
					+ "Nuestro equipo estará encantado de atenderte.\n\n" +

					"Gracias por tu comprensión y esperamos recibirte pronto.\n\n" +

					"Saludos,\n" + "Equipo de Café Aurora ☕\n\n" +

					"— Este es un mensaje automático, por favor no respondas a este correo —";

			emailService.sendEmail(reservation.getCustomerEmail(), subject, message);
			return new ResultResponse(true, "Reserva rechazada exitosamente");

		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse updateReservationStatusReceptionist(UpdateReservationStatusRequest request) {
		try {
			Reservation reservation = reservationRepository.findById(request.getIdReservation())
					.orElseThrow(() -> new RuntimeException("La reserva no existe"));

			if (reservation.getStatus() != ReservationStatus.CONFIRMADA) {
				return new ResultResponse(false, "Solo reservas CONFIRMADAS pueden actualizarse");
			}

			ReservationStatus newStatus = request.getStatus();

			if (newStatus != ReservationStatus.COMPLETADA && newStatus != ReservationStatus.NO_ASISTIO) {
				return new ResultResponse(false, "Estado no permitido. Solo COMPLETADA o NO_SHOW");
			}

			if (newStatus == ReservationStatus.COMPLETADA) {
				User recepcionista = userRepository.findById(request.getIdRecepcionista())
						.orElseThrow(() -> new RuntimeException("El recepcionista no existe"));
				reservation.setAttendedBy(recepcionista);
			}

			reservation.setStatus(newStatus);
			reservation.setUpdatedAt(LocalDateTime.now());
			reservationRepository.save(reservation);

			String message = newStatus == ReservationStatus.COMPLETADA ? "La reserva fue completada correctamente"
					: "La reserva fue marcada como no asistió";

			return new ResultResponse(true, message);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public List<Reservation> getTodayConfirmedReservations() {
		LocalDate today = LocalDate.now();
		return reservationRepository.getTodayConfirmedReservations(today);
	}

	public List<Reservation> getConfirmedByRecepcionist(UUID idRecepcionist) {
		return reservationRepository.findByStatusAndAttendedByIdUser(ReservationStatus.CONFIRMADA, idRecepcionist);
	}

	public List<Reservation> getRejectdByRecepcionist(UUID idRecepcionist) {
		return reservationRepository.findByStatusAndAttendedByIdUser(ReservationStatus.RECHAZADA, idRecepcionist);
	}
}
