package com.cafeAurora.util;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import org.springframework.core.io.ClassPathResource;

import com.cafeAurora.enums.ReservationStatus;
import com.cafeAurora.model.Reservation;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PdfGenerator {
    private static final BaseColor PRIMARY   = new BaseColor(217, 119,   6);
    private static final BaseColor LIGHT_BG  = new BaseColor(254, 243, 199);
    private static final BaseColor DARK_TEXT = new BaseColor( 31,  41,  55);
    private static final BaseColor GRAY_TEXT = new BaseColor(107, 114, 128);

    private static final Font FONT_TITLE    = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD,   PRIMARY);
    private static final Font FONT_SUBTITLE = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD,   DARK_TEXT);
    private static final Font FONT_LABEL    = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   GRAY_TEXT);
    private static final Font FONT_VALUE    = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, DARK_TEXT);

    public static byte[] generateReservationPdf(Reservation reservation) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, stream);
            document.open();

            addHeader(document);
            addTitleSection(document, reservation);
            addStatusBadge(document, reservation);
            addReservationDetails(document, reservation);
            addCustomerDetails(document, reservation);
            addSpecialNotes(document, reservation);
            addConfirmationInfo(document, reservation);
            addFooter(document);

            document.close();
            return stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addHeader(Document document) throws Exception {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 2});

        headerTable.addCell(buildLogoCell());
        headerTable.addCell(buildContactCell());
        document.add(headerTable);

        LineSeparator line = new LineSeparator();
        line.setLineColor(PRIMARY);
        line.setLineWidth(2);
        document.add(new Chunk(line));
        document.add(new Paragraph(" "));
    }

    private static void addTitleSection(Document document, Reservation reservation) throws Exception {
        Paragraph title = new Paragraph("Comprobante de Reserva", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph reservationId = new Paragraph(
                "#" + String.format("%05d", reservation.getIdReservation()),
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, GRAY_TEXT));
        reservationId.setAlignment(Element.ALIGN_CENTER);
        reservationId.setSpacingAfter(20);
        document.add(reservationId);
    }

    private static void addStatusBadge(Document document, Reservation reservation) throws Exception {
        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(30);
        statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        BaseColor statusColor = reservation.getStatus() == ReservationStatus.CONFIRMADA
                ? new BaseColor(16, 185, 129)
                : new BaseColor(251, 191, 36);

        PdfPCell statusCell = new PdfPCell();
        statusCell.setBackgroundColor(statusColor);
        statusCell.setBorder(Rectangle.NO_BORDER);
        statusCell.setPaddingTop(3);
        statusCell.setPaddingBottom(8);
        statusCell.setPaddingLeft(1);
        statusCell.setPaddingRight(2);
        statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        statusCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        String imgPath = reservation.getStatus() == ReservationStatus.CONFIRMADA
                ? "static/img/check.png"
                : "static/img/reloj.png";

        Image statusImg = Image.getInstance(new ClassPathResource(imgPath).getURL());
        statusImg.scaleToFit(14f, 14f);

        Paragraph statusParagraph = new Paragraph();
        statusParagraph.setFont(new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE));
        statusParagraph.add(new Chunk(statusImg, 0, -2));
        statusParagraph.add(" " + reservation.getStatus().toString());
        statusParagraph.setAlignment(Element.ALIGN_CENTER);

        statusCell.addElement(statusParagraph);
        statusTable.addCell(statusCell);

        document.add(statusTable);
        document.add(new Paragraph(" "));
    }

    private static void addReservationDetails(Document document, Reservation reservation) throws Exception {
        document.add(new Paragraph("Detalles de la Reserva", FONT_SUBTITLE));
        document.add(new Paragraph(" "));

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 1});
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(15);

        addInfoRow(infoTable, "Fecha",
                reservation.getReservationDate()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        addInfoRow(infoTable, "Hora",
                reservation.getReservationTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));

        addInfoRow(infoTable, "Personas",
                reservation.getNumPeople() + (reservation.getNumPeople() == 1 ? " persona" : " personas"));

        String mesa = reservation.getTable() != null
                ? "Mesa #" + reservation.getTable().getIdTable()
                : "Por asignar";
        addInfoRow(infoTable, "Mesa", mesa);

        document.add(infoTable);
    }

    private static void addCustomerDetails(Document document, Reservation reservation) throws Exception {
        document.add(new Paragraph("Datos del Cliente", FONT_SUBTITLE));
        document.add(new Paragraph(" "));

        PdfPTable customerTable = new PdfPTable(1);
        customerTable.setWidthPercentage(100);
        customerTable.setSpacingBefore(10);
        customerTable.setSpacingAfter(15);

        addSingleInfoRow(customerTable, "Nombre",    reservation.getCustomerName(),  LIGHT_BG);
        addSingleInfoRow(customerTable, "Email",     reservation.getCustomerEmail(), LIGHT_BG);
        addSingleInfoRow(customerTable, "Teléfono",  reservation.getCustomerPhone(), LIGHT_BG);

        document.add(customerTable);
    }

    private static void addSpecialNotes(Document document, Reservation reservation) throws Exception {
        if (reservation.getSpecialNotes() == null || reservation.getSpecialNotes().trim().isEmpty()) return;

        document.add(new Paragraph("Notas Especiales", FONT_SUBTITLE));
        document.add(new Paragraph(" "));

        PdfPCell notesCell = new PdfPCell();
        notesCell.setBackgroundColor(new BaseColor(254, 249, 195));
        notesCell.setPadding(12);
        notesCell.setBorder(Rectangle.NO_BORDER);
        notesCell.addElement(new Paragraph(reservation.getSpecialNotes(), FONT_VALUE));

        PdfPTable notesTable = new PdfPTable(1);
        notesTable.setWidthPercentage(100);
        notesTable.setSpacingBefore(10);
        notesTable.setSpacingAfter(15);
        notesTable.addCell(notesCell);

        document.add(notesTable);
    }

    private static void addConfirmationInfo(Document document, Reservation reservation) throws Exception {
        if (reservation.getStatus() != ReservationStatus.CONFIRMADA
                || reservation.getAttendedBy() == null) return;

        document.add(new Paragraph("Información Adicional", FONT_SUBTITLE));
        document.add(new Paragraph(" "));

        BaseColor greenBg = new BaseColor(220, 252, 231);

        PdfPTable additionalTable = new PdfPTable(1);
        additionalTable.setWidthPercentage(100);
        additionalTable.setSpacingBefore(10);
        additionalTable.setSpacingAfter(15);

        String staff = reservation.getAttendedBy().getName() != null
                ? reservation.getAttendedBy().getName()
                : "Staff";

        addSingleInfoRow(additionalTable, "Confirmada por", staff, greenBg);

        if (reservation.getResponseNotes() != null && !reservation.getResponseNotes().trim().isEmpty()) {
            addSingleInfoRow(additionalTable, "Respuesta", reservation.getResponseNotes(), greenBg);
        }

        document.add(additionalTable);
    }

    private static void addFooter(Document document) throws Exception {
        document.add(new Paragraph(" "));

        LineSeparator footerLine = new LineSeparator();
        footerLine.setLineColor(GRAY_TEXT);
        footerLine.setLineWidth(1);
        document.add(new Chunk(footerLine));
        document.add(new Paragraph(" "));

        Paragraph footerText = new Paragraph();
        footerText.add(new Chunk("Generado el: ",
                new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRAY_TEXT)));
        footerText.add(new Chunk(
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, DARK_TEXT)));
        footerText.setAlignment(Element.ALIGN_CENTER);
        document.add(footerText);

        Paragraph thankYou = new Paragraph(
                "¡Gracias por elegirnos! Te esperamos en Café Aurora",
                new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, PRIMARY));
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYou.setSpacingBefore(10);
        document.add(thankYou);
    }

    private static PdfPCell buildLogoCell() {
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPTable logoTextTable = new PdfPTable(2);
        try {
            logoTextTable.setWidths(new float[]{1, 3});
            logoTextTable.setWidthPercentage(100);
        } catch (DocumentException ignored) {}

        try {
            Image logo = Image.getInstance(new ClassPathResource("static/img/logo.png").getURL());
            logo.scaleToFit(42f, 42f);

            PdfPCell imgCell = new PdfPCell(logo);
            imgCell.setBorder(Rectangle.NO_BORDER);
            imgCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell brandCell = new PdfPCell(
                    new Paragraph("Café Aurora",
                            new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, PRIMARY)));
            brandCell.setBorder(Rectangle.NO_BORDER);
            brandCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            brandCell.setPaddingLeft(4);

            logoTextTable.addCell(imgCell);
            logoTextTable.addCell(brandCell);

        } catch (Exception e) {
            PdfPCell emojiCell = new PdfPCell(
                    new Paragraph("☕", new Font(Font.FontFamily.HELVETICA, 40, Font.NORMAL, PRIMARY)));
            emojiCell.setBorder(Rectangle.NO_BORDER);
            emojiCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            emojiCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell brandCell = new PdfPCell(
                    new Paragraph("Café Aurora",
                            new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, PRIMARY)));
            brandCell.setBorder(Rectangle.NO_BORDER);
            brandCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            logoTextTable.addCell(emojiCell);
            logoTextTable.addCell(brandCell);

            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        logoCell.addElement(logoTextTable);
        return logoCell;
    }

    private static PdfPCell buildContactCell() {
        PdfPCell contactCell = new PdfPCell();
        contactCell.setBorder(Rectangle.NO_BORDER);
        contactCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        contactCell.setPaddingTop(10);
        contactCell.setPaddingBottom(8);

        Paragraph contactInfo = new Paragraph();
        contactInfo.add(new Chunk("Calle 85 #15-32, Chapinero\n",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_TEXT)));
        contactInfo.add(new Chunk("Bogotá, Colombia\n",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_TEXT)));
        contactInfo.add(new Chunk("+57 1 234-5678\n",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_TEXT)));
        contactInfo.add(new Chunk("hola@cafeaurora.co",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, PRIMARY)));
        contactInfo.setAlignment(Element.ALIGN_RIGHT);

        contactCell.addElement(contactInfo);
        return contactCell;
    }

    private static void addInfoRow(PdfPTable table, String label, String value) throws DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(LIGHT_BG);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        cell.addElement(new Paragraph(label, FONT_LABEL));
        cell.addElement(new Paragraph(value, FONT_VALUE));
        table.addCell(cell);
    }

    private static void addSingleInfoRow(PdfPTable table, String label, String value,
                                         BaseColor bgColor) throws DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(12);

        Paragraph labelP = new Paragraph(label, FONT_LABEL);
        labelP.setSpacingAfter(4);
        cell.addElement(labelP);
        cell.addElement(new Paragraph(value, FONT_VALUE));

        table.addCell(cell);
    }
}