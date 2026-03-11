package com.cafeAurora.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import com.cafeAurora.dto.ReportReservationDTO;
import com.cafeAurora.dto.ReportTableCoffeDTO;
import com.cafeAurora.enums.ReservationSource;
import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.dto.ReportReceptionistDTO;
import com.cafeAurora.util.ReportExcelGenerator;
import com.cafeAurora.util.ReportPdfGenerator;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.repository.IReservationRepository;
import com.cafeAurora.util.PdfGenerator;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReservationService reservationService;
    private final IReservationRepository reservationRepository;

    public byte[] generateReservationPdf(Integer idReservation, UUID idUser) {
        Reservation reservation = reservationService.getReservationForUserById(idReservation, idUser);

        if (reservation == null) {
            throw new RuntimeException("No existe reserva con ese ID");
        }

        return PdfGenerator.generateReservationPdf(reservation);
    }
    
    /* ADMIN */
    public List<ReportReservationDTO> getReservations(
            LocalDate start, LocalDate end, String status, String source) {

        ReservationStatus statusEnum = null;
        ReservationSource sourceEnum = null;

        if (status != null && !status.isBlank()) {
            try {
                statusEnum = ReservationStatus.valueOf(status.toUpperCase());
            } catch (Exception ignored) {}
        }

        if (source != null && !source.isBlank()) {
            try {
                sourceEnum = ReservationSource.valueOf(source.toUpperCase());
            } catch (Exception ignored) {}
        }

        return reservationRepository.findReservationsByRange(start, end, statusEnum, sourceEnum);
    }

    public byte[] exportReservationsPdf(
            LocalDate start, LocalDate end, String status, String source) {

        List<ReportReservationDTO> data = getReservations(start, end, status, source);
        return ReportPdfGenerator.generateReservationsReport(
                data, start, end, status, source
        );
    }

    public byte[] exportReservationsExcel(
            LocalDate start, LocalDate end, String status, String source) {

        List<ReportReservationDTO> data = getReservations(start, end, status, source);
        return ReportExcelGenerator.generateReservationsReport(
                data, start, end, status, source
        );
    }

    public List<ReportTableCoffeDTO> getTableOccupation(LocalDate start, LocalDate end) {
        return reservationRepository.findTableOccupationByRange(start, end);
    }

    public byte[] exportTableOccupationPdf(LocalDate start, LocalDate end) {
        List<ReportTableCoffeDTO> data = getTableOccupation(start, end);
        return ReportPdfGenerator.generateTableOccupationReport(data, start, end);
    }

    public byte[] exportTableOccupationExcel(LocalDate start, LocalDate end) {
        List<ReportTableCoffeDTO> data = getTableOccupation(start, end);
        return ReportExcelGenerator.generateTableOccupationReport(data, start, end);
    }


    public List<ReportReceptionistDTO> getReceptionistPerformance(LocalDate start, LocalDate end) {
        return reservationRepository.findReceptionistPerformanceByRange(start, end);
    }

    public byte[] exportReceptionistPerformancePdf(LocalDate start, LocalDate end) {
        List<ReportReceptionistDTO> data = getReceptionistPerformance(start, end);
        return ReportPdfGenerator.generateReceptionistReport(data, start, end);
    }

    public byte[] exportReceptionistPerformanceExcel(LocalDate start, LocalDate end) {
        List<ReportReceptionistDTO> data = getReceptionistPerformance(start, end);
        return ReportExcelGenerator.generateReceptionistReport(data, start, end);
    }
}