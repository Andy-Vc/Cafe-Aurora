package com.cafeAurora.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.cafeAurora.dto.ConfirmReservationRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Reservation;
import com.cafeAurora.service.ReservationService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	/* RECEPCIONIST */
	@GetMapping("/list/pendientes")
	public ResponseEntity<?> getPendientes() {
		try {
			List<Reservation> pendientes = reservationService.getReservationPendiente();

			if (pendientes.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay reservas pendientes.");
			}

			return ResponseEntity.ok(pendientes);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener reservas pendientes.");
		}
	}

	@PutMapping("/confirm/{idReservation}")
	public ResponseEntity<ResultResponse> confirmReservation(
			@PathVariable Integer idReservation,
			@RequestBody ConfirmReservationRequest request
	) {
		try {
			request.setIdReservation(idReservation);

			ResultResponse result = reservationService.confirmReservation(request);

			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResultResponse(false, e.getMessage()));
		}
	}


	@GetMapping("/list/confirmed/{idRecepcionist}")
	public ResponseEntity<?> getConfirmedByRecepcionist(@PathVariable UUID idRecepcionist) {
		try {
			List<Reservation> confirmed = reservationService.getConfirmedByRecepcionist(idRecepcionist);

			if (confirmed.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("El recepcionista no tiene reservas confirmadas.");
			}

			return ResponseEntity.ok(confirmed);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener reservas confirmadas.");
		}
	}

	/* CUSTOMER */
	@PostMapping("/register")
	public ResponseEntity<ResultResponse> createReservation(@RequestBody Reservation reservation) {
		try {
			ResultResponse result = reservationService.createReservation(reservation);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/list/active/{idUser}")
	public ResponseEntity<?> getActiveReservations(@PathVariable UUID idUser) {
		try {
			List<Reservation> reservations = reservationService.getActiveReservationsForUser(idUser);

			if (reservations.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay reservas activas.");
			}

			return ResponseEntity.ok(reservations);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener reservas activas.");
		}
	}

	@GetMapping("/list/history/{idUser}")
	public ResponseEntity<?> getHistoryReservations(@PathVariable UUID idUser) {
		try {
			List<Reservation> reservations = reservationService.getHistoryForUser(idUser);

			if (reservations.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("El historial está vacío.");
			}

			return ResponseEntity.ok(reservations);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener historial.");
		}
	}

	@GetMapping("/count/{idUser}")
	public ResponseEntity<?> countReservationsByUser(@PathVariable UUID idUser) {
		try {
			long total = reservationService.countReservationsForUser(idUser);

			return ResponseEntity.ok(total);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al contar las reservas.");
		}
	}

	@GetMapping("/pdf/{idReservation}/user/{idUser}")
	public ResponseEntity<byte[]> generateReservationPdf(@PathVariable Integer idReservation,
			@PathVariable UUID idUser) {
		try {
			Reservation reservation = reservationService.getReservationForUserById(idReservation, idUser);

			if (reservation == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			if (reservation.getStatus() != ReservationStatus.PENDIENTE
					&& reservation.getStatus() != ReservationStatus.CONFIRMADA) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Solo se puede generar PDF para reservas pendientes o confirmadas".getBytes());
			}

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			PdfWriter.getInstance(document, stream);
			document.open();

			BaseColor primaryColor = new BaseColor(217, 119, 6);
			BaseColor lightBg = new BaseColor(254, 243, 199);
			BaseColor darkText = new BaseColor(31, 41, 55);
			BaseColor lightText = new BaseColor(107, 114, 128);

			Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor);
			Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, darkText);
			Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, lightText);
			Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, darkText);

			PdfPTable headerTable = new PdfPTable(2);
			headerTable.setWidthPercentage(100);
			headerTable.setWidths(new float[] { 1, 2 });

			PdfPCell logoCell = new PdfPCell();
			logoCell.setBorder(Rectangle.NO_BORDER);
			logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			PdfPTable logoTextTable = new PdfPTable(2);
			logoTextTable.setWidths(new float[] { 1, 3 });
			logoTextTable.setWidthPercentage(100);
			try {
				ClassPathResource imgFile = new ClassPathResource("static/img/logo.png");
				Image logo = Image.getInstance(imgFile.getURL());
				logo.scaleToFit(42f, 42f);

				PdfPCell imgCell = new PdfPCell(logo);
				imgCell.setBorder(Rectangle.NO_BORDER);
				imgCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				Paragraph brand = new Paragraph("Café Aurora",
						new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, primaryColor));

				PdfPCell textCell = new PdfPCell(brand);
				textCell.setBorder(Rectangle.NO_BORDER);
				textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				textCell.setPaddingLeft(4);

				logoTextTable.addCell(imgCell);
				logoTextTable.addCell(textCell);
				logoCell.addElement(logoTextTable);

			} catch (Exception e) {

				PdfPTable logoRow = new PdfPTable(2);
				logoRow.setWidthPercentage(100);
				logoRow.setWidths(new float[] { 1, 3 });

				PdfPCell fallbackLogoCell = new PdfPCell(
						new Paragraph("☕", new Font(Font.FontFamily.HELVETICA, 40, Font.NORMAL, primaryColor)));
				fallbackLogoCell.setBorder(Rectangle.NO_BORDER);
				fallbackLogoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				fallbackLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				PdfPCell brandCell = new PdfPCell(
						new Paragraph("Café Aurora", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, primaryColor)));
				brandCell.setBorder(Rectangle.NO_BORDER);
				brandCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				logoRow.addCell(fallbackLogoCell);
				logoRow.addCell(brandCell);
				logoCell.addElement(logoRow);

				System.err.println("No se pudo cargar el logo: " + e.getMessage());
			}

			headerTable.addCell(logoCell);

			PdfPCell contactCell = new PdfPCell();
			contactCell.setBorder(Rectangle.NO_BORDER);
			contactCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			contactCell.setPaddingTop(10);
			contactCell.setPaddingBottom(8);

			Paragraph contactInfo = new Paragraph();
			contactInfo.add(new Chunk("Calle 85 #15-32, Chapinero\n",
					new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, darkText)));
			contactInfo.add(
					new Chunk("Bogotá, Colombia\n", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, darkText)));
			contactInfo
					.add(new Chunk("+57 1 234-5678\n", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, darkText)));
			contactInfo.add(new Chunk("hola@cafeaurora.co",
					new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, primaryColor)));
			contactInfo.setAlignment(Element.ALIGN_RIGHT);
			contactCell.addElement(contactInfo);

			headerTable.addCell(contactCell);
			document.add(headerTable);

			LineSeparator line = new LineSeparator();
			line.setLineColor(primaryColor);
			line.setLineWidth(2);
			document.add(new Chunk(line));
			document.add(new Paragraph(" "));

			Paragraph title = new Paragraph("Comprobante de Reserva", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			Paragraph reservationId = new Paragraph("#" + String.format("%05d", reservation.getIdReservation()),
					new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, lightText));
			reservationId.setAlignment(Element.ALIGN_CENTER);
			reservationId.setSpacingAfter(20);
			document.add(reservationId);

			PdfPTable statusTable = new PdfPTable(1);
			statusTable.setWidthPercentage(30);
			statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell statusCell = new PdfPCell();
			BaseColor statusColor = reservation.getStatus() == ReservationStatus.CONFIRMADA
					? new BaseColor(16, 185, 129)
					: new BaseColor(251, 191, 36);
			statusCell.setBackgroundColor(statusColor);
			statusCell.setBorder(Rectangle.NO_BORDER);
			statusCell.setPaddingTop(3);
			statusCell.setPaddingBottom(8);
			statusCell.setPaddingLeft(1);
			statusCell.setPaddingRight(2);
			statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			Image statusImage;
			if (reservation.getStatus() == ReservationStatus.CONFIRMADA) {
				statusImage = Image.getInstance(new ClassPathResource("static/img/check.png").getURL());
			} else {
				statusImage = Image.getInstance(new ClassPathResource("static/img/reloj.png").getURL());
			}

			statusImage.scaleToFit(14f, 14f);
			statusImage.setAlignment(Image.ALIGN_MIDDLE);
			Chunk imgChunk = new Chunk(statusImage, 0, -2);
			Paragraph statusParagraph = new Paragraph();
			statusParagraph.add(imgChunk);
			statusParagraph.add(" " + reservation.getStatus().toString());
			statusParagraph.setFont(new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE));
			statusParagraph.setAlignment(Element.ALIGN_CENTER);
			statusCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			statusCell.addElement(statusParagraph);

			statusTable.addCell(statusCell);
			document.add(statusTable);
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Detalles de la Reserva", subtitleFont));
			document.add(new Paragraph(" "));

			PdfPTable infoTable = new PdfPTable(2);
			infoTable.setWidthPercentage(100);
			infoTable.setWidths(new float[] { 1, 1 });
			infoTable.setSpacingBefore(10);
			infoTable.setSpacingAfter(15);

			addInfoRow(infoTable, "Fecha",
					reservation.getReservationDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
					labelFont, valueFont, lightBg);

			addInfoRow(infoTable, "Hora",
					reservation.getReservationTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
					labelFont, valueFont, lightBg);

			addInfoRow(infoTable, "Personas",
					reservation.getNumPeople() + (reservation.getNumPeople() == 1 ? " persona" : " personas"),
					labelFont, valueFont, lightBg);

			String tableText = (reservation.getTable() != null) ? "Mesa #" + reservation.getTable().getIdTable()
					: "Por asignar";
			addInfoRow(infoTable, "Mesa", tableText, labelFont, valueFont, lightBg);

			document.add(infoTable);
			document.add(new Paragraph("Datos del Cliente", subtitleFont));
			document.add(new Paragraph(" "));

			PdfPTable customerTable = new PdfPTable(1);
			customerTable.setWidthPercentage(100);
			customerTable.setSpacingBefore(10);
			customerTable.setSpacingAfter(15);

			addSingleInfoRow(customerTable, "Nombre", reservation.getCustomerName(), labelFont, valueFont, lightBg);
			addSingleInfoRow(customerTable, "Email", reservation.getCustomerEmail(), labelFont, valueFont, lightBg);
			addSingleInfoRow(customerTable, "Teléfono", reservation.getCustomerPhone(), labelFont, valueFont, lightBg);

			document.add(customerTable);

			if (reservation.getSpecialNotes() != null && !reservation.getSpecialNotes().trim().isEmpty()) {
				document.add(new Paragraph("Notas Especiales", subtitleFont));
				document.add(new Paragraph(" "));

				PdfPTable notesTable = new PdfPTable(1);
				notesTable.setWidthPercentage(100);
				notesTable.setSpacingBefore(10);
				notesTable.setSpacingAfter(15);

				PdfPCell notesCell = new PdfPCell();
				notesCell.setBackgroundColor(new BaseColor(254, 249, 195));
				notesCell.setPadding(12);
				notesCell.setBorder(Rectangle.NO_BORDER);

				Paragraph notesText = new Paragraph(reservation.getSpecialNotes(), valueFont);
				notesCell.addElement(notesText);

				notesTable.addCell(notesCell);
				document.add(notesTable);
			}

			if (reservation.getStatus() == ReservationStatus.CONFIRMADA && reservation.getAttendedBy() != null) {
				document.add(new Paragraph("Información Adicional", subtitleFont));
				document.add(new Paragraph(" "));

				PdfPTable additionalTable = new PdfPTable(1);
				additionalTable.setWidthPercentage(100);
				additionalTable.setSpacingBefore(10);
				additionalTable.setSpacingAfter(15);

				addSingleInfoRow(additionalTable, "Confirmada por",
						reservation.getAttendedBy().getName() != null ? reservation.getAttendedBy().getName() : "Staff",
						labelFont, valueFont, new BaseColor(220, 252, 231)); // Verde claro

				if (reservation.getResponseNotes() != null && !reservation.getResponseNotes().trim().isEmpty()) {
					addSingleInfoRow(additionalTable, "Respuesta", reservation.getResponseNotes(), labelFont, valueFont,
							new BaseColor(220, 252, 231));
				}

				document.add(additionalTable);
			}

			document.add(new Paragraph(" "));
			LineSeparator footerLine = new LineSeparator();
			footerLine.setLineColor(lightText);
			footerLine.setLineWidth(1);
			document.add(new Chunk(footerLine));
			document.add(new Paragraph(" "));

			Paragraph footerText = new Paragraph();
			footerText.add(new Chunk("Generado el: ", new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, lightText)));
			footerText.add(new Chunk(
					LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
					new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, darkText)));
			footerText.setAlignment(Element.ALIGN_CENTER);
			document.add(footerText);

			Paragraph thankYou = new Paragraph("¡Gracias por elegirnos! Te esperamos en Café Aurora",
					new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, primaryColor));
			thankYou.setAlignment(Element.ALIGN_CENTER);
			thankYou.setSpacingBefore(10);
			document.add(thankYou);

			document.close();

			byte[] pdfBytes = stream.toByteArray();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "reserva_cafeaurora_" + idReservation + ".pdf");

			return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont,
			BaseColor bgColor) throws DocumentException {

		PdfPCell cell1 = new PdfPCell();
		cell1.setBackgroundColor(bgColor);
		cell1.setBorder(Rectangle.NO_BORDER);
		cell1.setPadding(10);

		Paragraph labelPar = new Paragraph(label, labelFont);
		cell1.addElement(labelPar);
		Paragraph valuePar = new Paragraph(value, valueFont);
		cell1.addElement(valuePar);

		table.addCell(cell1);
	}

	private void addSingleInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont,
			BaseColor bgColor) throws DocumentException {

		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(bgColor);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(12);

		Paragraph labelPar = new Paragraph(label, labelFont);
		labelPar.setSpacingAfter(4);
		cell.addElement(labelPar);

		Paragraph valuePar = new Paragraph(value, valueFont);
		cell.addElement(valuePar);

		table.addCell(cell);
	}
}
