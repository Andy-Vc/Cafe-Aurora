package com.cafeAurora.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cafeAurora.model.Reservation;
import com.cafeAurora.util.PdfGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReservationService reservationService;

    public byte[] generateReservationPdf(Integer idReservation, UUID idUser) {
        Reservation reservation = reservationService.getReservationForUserById(idReservation, idUser);

        if (reservation == null) {
            throw new RuntimeException("No existe reserva con ese ID");
        }

        return PdfGenerator.generateReservationPdf(reservation);
    }
}