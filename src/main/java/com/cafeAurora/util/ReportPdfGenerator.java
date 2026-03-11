package com.cafeAurora.util;

import com.cafeAurora.dto.ReportReservationDTO;
import com.cafeAurora.dto.ReportTableCoffeDTO;
import com.cafeAurora.dto.ReportReceptionistDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportPdfGenerator {
	private static final BaseColor PRIMARY = new BaseColor(217, 119, 6);
	private static final BaseColor DARK_TEXT = new BaseColor(31, 41, 55);
	private static final BaseColor GRAY_TEXT = new BaseColor(107, 114, 128);
	private static final BaseColor HEADER_BG = new BaseColor(245, 158, 11);

	private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, PRIMARY);
	private static final Font FONT_SUBTITLE = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, GRAY_TEXT);
	private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
	private static final Font FONT_CELL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, DARK_TEXT);
	private static final Font FONT_FOOTER = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, GRAY_TEXT);

	private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	public static byte[] generateReservationsReport(List<ReportReservationDTO> data, LocalDate start, LocalDate end,
			String status, String source) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
			PdfWriter.getInstance(document, stream);
			document.open();

			addHeader(document, "Reporte de Reservas", start, end);
			// Filtros activos
			if (status != null || source != null) {
				Paragraph filters = new Paragraph();
				filters.setFont(FONT_SUBTITLE);
				if (status != null)
					filters.add("Estado: " + status + "   ");
				if (source != null)
					filters.add("Origen: " + source);
				filters.setSpacingAfter(8);
				document.add(filters);
			}

			// Tabla
			String[] headers = { "#", "Cliente", "Fecha", "Hora", "Personas", "Mesa", "Ubicación", "Estado", "Origen" };
			float[] widths = { 4, 18, 9, 7, 8, 7, 10, 11, 9 };
			PdfPTable table = buildTableHeader(headers, widths);

			boolean alt = false;
			for (ReportReservationDTO r : data) {
				BaseColor bg = alt ? new BaseColor(249, 250, 251) : BaseColor.WHITE;
				addRow(table, bg, String.valueOf(r.getIdReservation()), r.getCustomerName(),
						r.getReservationDate().format(FMT_DATE), r.getReservationTime().toString(),
						String.valueOf(r.getNumPeople()),
						r.getTableNumber() != null ? "Mesa " + r.getTableNumber() : "-",
						r.getTableLocation() != null ? r.getTableLocation() : "-", r.getStatus().toString(),
						r.getSource().toString());
				alt = !alt;
			}
			document.add(table);
			addSummary(document, "Total reservaciones: " + data.size());
			addFooter(document);

			document.close();
			return stream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] generateTableOccupationReport(List<ReportTableCoffeDTO> data, LocalDate start, LocalDate end) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4, 40, 40, 40, 40);
			PdfWriter.getInstance(document, stream);
			document.open();

			addHeader(document, "Reporte de Ocupación de Mesas", start, end);

			String[] headers = { "Mesa", "Ubicación", "Capacidad", "Total", "Confirmadas", "Canceladas", "No asistió",
					"Completadas" };
			float[] widths = { 10, 15, 10, 10, 13, 13, 10, 13 };
			PdfPTable table = buildTableHeader(headers, widths);

			boolean alt = false;
			for (ReportTableCoffeDTO t : data) {
				BaseColor bg = alt ? new BaseColor(249, 250, 251) : BaseColor.WHITE;
				addRow(table, bg, "Table " + t.getTableNumber(), t.getLocation() != null ? t.getLocation() : "-",
						String.valueOf(t.getCapacity()), String.valueOf(t.getTotalReservations()),
						String.valueOf(t.getConfirmed()), String.valueOf(t.getCancelled()),
						String.valueOf(t.getNoShow()), String.valueOf(t.getCompleted()));
				alt = !alt;
			}
			document.add(table);
			addFooter(document);

			document.close();
			return stream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] generateReceptionistReport(List<ReportReceptionistDTO> data, LocalDate start, LocalDate end) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4, 40, 40, 40, 40);
			PdfWriter.getInstance(document, stream);
			document.open();

			addHeader(document, "Reporte de Desempeño del Personal de Recepción", start, end);

			String[] headers = { "Nombre", "Correo electrónico", "Reservas Atendidas", "Confirmadas", "Rechazadas",
					"Completadas" };
			float[] widths = { 18, 28, 14, 13, 13, 14 };
			PdfPTable table = buildTableHeader(headers, widths);

			boolean alt = false;
			for (ReportReceptionistDTO r : data) {
				BaseColor bg = alt ? new BaseColor(249, 250, 251) : BaseColor.WHITE;
				addRow(table, bg, r.getReceptName(), r.getReceptEmail(), String.valueOf(r.getTotalAttended()),
						String.valueOf(r.getConfirmed()), String.valueOf(r.getRejected()),
						String.valueOf(r.getCompleted()));
				alt = !alt;
			}
			document.add(table);
			addSummary(document, "Total recepcionistas: " + data.size());
			addFooter(document);

			document.close();
			return stream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void addHeader(Document document, String title, LocalDate start, LocalDate end) throws Exception {
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[] { 1, 2 });
		headerTable.addCell(buildLogoCell());
		headerTable.addCell(buildContactCell());
		document.add(headerTable);

		LineSeparator line = new LineSeparator();
		line.setLineColor(PRIMARY);
		line.setLineWidth(2);
		document.add(new Chunk(line));
		document.add(new Paragraph(" "));

		Paragraph titleP = new Paragraph(title, FONT_TITLE);
		titleP.setAlignment(Element.ALIGN_CENTER);
		document.add(titleP);

		Paragraph period = new Paragraph("Periodo: " + start.format(FMT_DATE) + " – " + end.format(FMT_DATE),
				FONT_SUBTITLE);
		period.setAlignment(Element.ALIGN_CENTER);
		period.setSpacingAfter(14);
		document.add(period);
	}

	private static PdfPTable buildTableHeader(String[] headers, float[] widths) throws DocumentException {
		PdfPTable table = new PdfPTable(headers.length);
		table.setWidthPercentage(100);
		table.setWidths(widths);
		table.setSpacingBefore(6);
		table.setSpacingAfter(10);

		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, FONT_HEADER));
			cell.setBackgroundColor(HEADER_BG);
			cell.setPadding(7);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		return table;
	}

	private static void addRow(PdfPTable table, BaseColor bg, String... values) {
		for (String v : values) {
			PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "-", FONT_CELL));
			cell.setBackgroundColor(bg);
			cell.setPadding(6);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setBorderWidthBottom(0.5f);
			cell.setBorderColorBottom(new BaseColor(229, 231, 235));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
	}

	private static void addSummary(Document document, String text) throws DocumentException {
		Paragraph summary = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, DARK_TEXT));
		summary.setAlignment(Element.ALIGN_RIGHT);
		summary.setSpacingBefore(4);
		document.add(summary);
	}

	private static void addFooter(Document document) throws DocumentException {
		document.add(new Paragraph(" "));
		LineSeparator footerLine = new LineSeparator();
		footerLine.setLineColor(GRAY_TEXT);
		footerLine.setLineWidth(1);
		document.add(new Chunk(footerLine));
		document.add(new Paragraph(" "));

		Paragraph footerText = new Paragraph(
				"Generado: " + LocalDateTime.now().format(FMT_DT) + "   |   Café Aurora – Reporte Administrativo",
				FONT_FOOTER);
		footerText.setAlignment(Element.ALIGN_CENTER);
		document.add(footerText);
	}

	private static PdfPCell buildLogoCell() {
		PdfPCell logoCell = new PdfPCell();
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		PdfPTable logoTextTable = new PdfPTable(2);
		try {
			logoTextTable.setWidths(new float[] { 1, 3 });
		} catch (DocumentException ignored) {
		}

		try {
			Image logo = Image.getInstance(new ClassPathResource("static/img/logo.png").getURL());
			logo.scaleToFit(42f, 42f);

			PdfPCell imgCell = new PdfPCell(logo);
			imgCell.setBorder(Rectangle.NO_BORDER);
			imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell brandCell = new PdfPCell(
					new Paragraph("Café Aurora", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, PRIMARY)));
			brandCell.setBorder(Rectangle.NO_BORDER);
			brandCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			brandCell.setPaddingLeft(4);

			logoTextTable.addCell(imgCell);
			logoTextTable.addCell(brandCell);
		} catch (Exception e) {
			PdfPCell brandCell = new PdfPCell(
					new Paragraph("Café Aurora", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, PRIMARY)));
			brandCell.setBorder(Rectangle.NO_BORDER);
			PdfPCell empty = new PdfPCell(new Paragraph(""));
			empty.setBorder(Rectangle.NO_BORDER);
			logoTextTable.addCell(empty);
			logoTextTable.addCell(brandCell);
		}

		logoCell.addElement(logoTextTable);
		return logoCell;
	}

	private static PdfPCell buildContactCell() {
		PdfPCell contactCell = new PdfPCell();
		contactCell.setBorder(Rectangle.NO_BORDER);
		contactCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		contactCell.setPaddingTop(10);

		Paragraph contactInfo = new Paragraph();
		contactInfo.add(new Chunk("Calle 85 #15-32, Chapinero\n",
				new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_TEXT)));
		contactInfo
				.add(new Chunk("Bogotá, Colombia\n", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_TEXT)));
		contactInfo.add(new Chunk("+57 1 234-5678\n", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_TEXT)));
		contactInfo.add(new Chunk("hola@cafeaurora.co", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, PRIMARY)));
		contactInfo.setAlignment(Element.ALIGN_RIGHT);

		contactCell.addElement(contactInfo);
		return contactCell;
	}
}