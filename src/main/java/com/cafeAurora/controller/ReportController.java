package com.cafeAurora.controller;

import java.util.UUID;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.cafeAurora.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

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
}