package com.cafeAurora.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.cafeAurora.dto.ReportReceptionistDTO;
import com.cafeAurora.dto.ReportReservationDTO;
import com.cafeAurora.dto.ReportTableCoffeDTO;
import com.cafeAurora.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    
    /* CUSTOMER */
    @GetMapping("/reservation/{idReservation}/user/{idUser}")
    public ResponseEntity<byte[]> getReservationPdf(
            @PathVariable Integer idReservation,
            @PathVariable UUID idUser) {

        try {
            byte[] pdf = reportService.generateReservationPdf(idReservation, idUser);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                "attachment", 
                "reserva_" + idReservation + ".pdf"
            );

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /* ADMIN */
    @GetMapping("/reservations")
    public ResponseEntity<List<ReportReservationDTO>> getReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source) {

        return ResponseEntity.ok(reportService.getReservations(start, end, status, source));
    }

    @GetMapping("/reservations/pdf")
    public ResponseEntity<byte[]> exportReservationsPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source) {

        byte[] pdf = reportService.exportReservationsPdf(start, end, status, source);
        return buildFileResponse(pdf, "reservations.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/reservations/excel")
    public ResponseEntity<byte[]> exportReservationsExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source) {

        byte[] excel = reportService.exportReservationsExcel(start, end, status, source);
        return buildFileResponse(excel, "reservations.xlsx", MEDIA_XLSX);
    }

    @GetMapping("/tables")
    public ResponseEntity<List<ReportTableCoffeDTO>> getTableOccupation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(reportService.getTableOccupation(start, end));
    }

    @GetMapping("/tables/pdf")
    public ResponseEntity<byte[]> exportTablesPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        byte[] pdf = reportService.exportTableOccupationPdf(start, end);
        return buildFileResponse(pdf, "table-occupation.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/tables/excel")
    public ResponseEntity<byte[]> exportTablesExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        byte[] excel = reportService.exportTableOccupationExcel(start, end);
        return buildFileResponse(excel, "table-occupation.xlsx", MEDIA_XLSX);
    }

    @GetMapping("/receptionists")
    public ResponseEntity<List<ReportReceptionistDTO>> getReceptionistPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(reportService.getReceptionistPerformance(start, end));
    }

    @GetMapping("/receptionists/pdf")
    public ResponseEntity<byte[]> exportReceptionistsPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        byte[] pdf = reportService.exportReceptionistPerformancePdf(start, end);
        return buildFileResponse(pdf, "receptionist-performance.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/receptionists/excel")
    public ResponseEntity<byte[]> exportReceptionistsExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        byte[] excel = reportService.exportReceptionistPerformanceExcel(start, end);
        return buildFileResponse(excel, "receptionist-performance.xlsx", MEDIA_XLSX);
    }

    private static final MediaType MEDIA_XLSX =
        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private ResponseEntity<byte[]> buildFileResponse(byte[] data, String filename, MediaType mediaType) {
        if (data == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(mediaType)
            .contentLength(data.length)
            .body(data);
    }
}