package com.cafeAurora.util;

import com.cafeAurora.dto.ReportReservationDTO;
import com.cafeAurora.dto.ReportTableCoffeDTO;
import com.cafeAurora.dto.ReportReceptionistDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportExcelGenerator {

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final byte[] COLOR_HEADER_BG = hexToBytes("F59E0B");
	private static final byte[] COLOR_HEADER_FG = hexToBytes("FFFFFF");
	private static final byte[] COLOR_TITLE_FG = hexToBytes("D97706");
	private static final byte[] COLOR_ALT_ROW = hexToBytes("FFFBEB");
	private static final byte[] COLOR_BORDER = hexToBytes("E5E7EB");

	public static byte[] generateReservationsReport(List<ReportReservationDTO> data, LocalDate start, LocalDate end,
			String status, String source) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Reservas");
			sheet.setDefaultColumnWidth(16);

			int rowIdx = 0;
			rowIdx = addTitleRow(wb, sheet, "Reporte de Reservas", rowIdx, 9);
			rowIdx = addPeriodRow(wb, sheet, start, end, rowIdx, 9);

			if (status != null || source != null) {
				Row filterRow = sheet.createRow(rowIdx++);
				StringBuilder sb = new StringBuilder("Filtros: ");
				if (status != null)
					sb.append("Estado=").append(status).append("  ");
				if (source != null)
					sb.append("Origen=").append(source);
				createStyledCell(filterRow, 0, sb.toString(), buildSubtitleStyle(wb));
				sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 8));
			}

			rowIdx++;

			String[] headers = { "ID", "Cliente", "Correo", "Teléfono", "Fecha", "Hora", "Personas", "Mesa",
					"Ubicación", "Estado", "Origen", "Notas" };
			rowIdx = addHeaderRow(wb, sheet, headers, rowIdx);

			CellStyle normalStyle = buildDataStyle(wb, false);
			CellStyle altStyle = buildDataStyle(wb, true);

			for (ReportReservationDTO r : data) {
				Row row = sheet.createRow(rowIdx++);
				CellStyle s = (rowIdx % 2 == 0) ? altStyle : normalStyle;

				createStyledCell(row, 0, r.getIdReservation(), s);
				createStyledCell(row, 1, r.getCustomerName(), s);
				createStyledCell(row, 2, nvl(r.getCustomerEmail()), s);
				createStyledCell(row, 3, nvl(r.getCustomerPhone()), s);
				createStyledCell(row, 4, r.getReservationDate().format(FMT), s);
				createStyledCell(row, 5, r.getReservationTime().toString(), s);
				createStyledCell(row, 6, r.getNumPeople(), s);
				createStyledCell(row, 7, r.getTableNumber() != null ? "Mesa " + r.getTableNumber() : "-", s);
				createStyledCell(row, 8, nvl(r.getTableLocation()), s);
				createStyledCell(row, 9, r.getStatus(), s);
				createStyledCell(row, 10, r.getSource(), s);
				createStyledCell(row, 11, nvl(r.getSpecialNotes()), s);
			}

			rowIdx++;
			Row totalRow = sheet.createRow(rowIdx);
			CellStyle totalStyle = buildTotalStyle(wb);
			createStyledCell(totalRow, 0, "Total: " + data.size() + " reservaciones", totalStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 3));

			autoSize(sheet, 12);
			return toBytes(wb);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] generateTableOccupationReport(List<ReportTableCoffeDTO> data, LocalDate start, LocalDate end) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Ocupación de Mesas");
			sheet.setDefaultColumnWidth(16);

			int rowIdx = 0;
			rowIdx = addTitleRow(wb, sheet, "Reporte de Ocupación de Mesas", rowIdx, 8);
			rowIdx = addPeriodRow(wb, sheet, start, end, rowIdx, 8);
			rowIdx++;

			String[] headers = { "Mesa", "Ubicación", "Capacidad", "Total Reservas", "Confirmadas", "Canceladas",
					"No asistió", "Completadas" };

			rowIdx = addHeaderRow(wb, sheet, headers, rowIdx);

			CellStyle normalStyle = buildDataStyle(wb, false);
			CellStyle altStyle = buildDataStyle(wb, true);

			for (ReportTableCoffeDTO t : data) {
				Row row = sheet.createRow(rowIdx++);
				CellStyle s = (rowIdx % 2 == 0) ? altStyle : normalStyle;

				createStyledCell(row, 0, "Mesa " + t.getTableNumber(), s);
				createStyledCell(row, 1, nvl(t.getLocation()), s);
				createStyledCell(row, 2, t.getCapacity(), s);
				createStyledCell(row, 3, t.getTotalReservations(), s);
				createStyledCell(row, 4, t.getConfirmed(), s);
				createStyledCell(row, 5, t.getCancelled(), s);
				createStyledCell(row, 6, t.getNoShow(), s);
				createStyledCell(row, 7, t.getCompleted(), s);
			}

			autoSize(sheet, 8);
			return toBytes(wb);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] generateReceptionistReport(List<ReportReceptionistDTO> data, LocalDate start, LocalDate end) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Desempeño de Recepcionistas");
			sheet.setDefaultColumnWidth(18);

			int rowIdx = 0;
			rowIdx = addTitleRow(wb, sheet, "Reporte de Desempeño de Recepcionistas", rowIdx, 6);
			rowIdx = addPeriodRow(wb, sheet, start, end, rowIdx, 6);
			rowIdx++;

			String[] headers = { 
				"Nombre", 
				"Correo electrónico", 
				"Total Atendidas", 
				"Confirmadas", 
				"Rechazadas", 
				"Completadas" 
			};

			rowIdx = addHeaderRow(wb, sheet, headers, rowIdx);

			CellStyle normalStyle = buildDataStyle(wb, false);
			CellStyle altStyle = buildDataStyle(wb, true);

			for (ReportReceptionistDTO r : data) {
				Row row = sheet.createRow(rowIdx++);
				CellStyle s = (rowIdx % 2 == 0) ? altStyle : normalStyle;

				createStyledCell(row, 0, r.getReceptName(), s);
				createStyledCell(row, 1, r.getReceptEmail(), s);
				createStyledCell(row, 2, r.getTotalAttended(), s);
				createStyledCell(row, 3, r.getConfirmed(), s);
				createStyledCell(row, 4, r.getRejected(), s);
				createStyledCell(row, 5, r.getCompleted(), s);
			}

			rowIdx++;
			Row totalRow = sheet.createRow(rowIdx);
			CellStyle totalStyle = buildTotalStyle(wb);
			createStyledCell(totalRow, 0, "Total recepcionistas: " + data.size(), totalStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 2));

			autoSize(sheet, 6);
			return toBytes(wb);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static int addTitleRow(XSSFWorkbook wb, XSSFSheet sheet, String title, int rowIdx, int cols) {
		Row row = sheet.createRow(rowIdx++);
		row.setHeightInPoints(28);
		CellStyle style = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		font.setColor(new XSSFColor(COLOR_TITLE_FG, null));
		style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		createStyledCell(row, 0, title, style);
		sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, cols - 1));
		return rowIdx;
	}

	private static int addPeriodRow(XSSFWorkbook wb, XSSFSheet sheet, LocalDate start, LocalDate end, int rowIdx,
			int cols) {
		Row row = sheet.createRow(rowIdx++);
		CellStyle style = buildSubtitleStyle(wb);
		createStyledCell(row, 0, "Periodo: " + start.format(FMT) + " – " + end.format(FMT), style);
		sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, cols - 1));
		return rowIdx;
	}

	private static int addHeaderRow(XSSFWorkbook wb, XSSFSheet sheet, String[] headers, int rowIdx) {
		Row row = sheet.createRow(rowIdx++);
		row.setHeightInPoints(20);
		CellStyle style = buildHeaderStyle(wb);
		for (int i = 0; i < headers.length; i++) {
			createStyledCell(row, i, headers[i], style);
		}
		return rowIdx;
	}

	private static CellStyle buildHeaderStyle(XSSFWorkbook wb) {
		XSSFCellStyle style = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setBold(true);
		font.setColor(new XSSFColor(COLOR_HEADER_FG, null));
		font.setFontHeightInPoints((short) 10);
		style.setFont(font);
		style.setFillForegroundColor(new XSSFColor(COLOR_HEADER_BG, null));
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		return style;
	}

	private static CellStyle buildDataStyle(XSSFWorkbook wb, boolean alt) {
		XSSFCellStyle style = wb.createCellStyle();
		if (alt) {
			style.setFillForegroundColor(new XSSFColor(COLOR_ALT_ROW, null));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(new XSSFColor(COLOR_BORDER, null));
		return style;
	}

	private static CellStyle buildSubtitleStyle(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setItalic(true);
		font.setFontHeightInPoints((short) 10);
		style.setFont(font);
		return style;
	}

	private static CellStyle buildTotalStyle(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		return style;
	}

	private static void createStyledCell(Row row, int col, Object value, CellStyle style) {
		Cell cell = row.createCell(col);
		if (value instanceof Number) {
			cell.setCellValue(((Number) value).doubleValue());
		} else {
			cell.setCellValue(value != null ? value.toString() : "");
		}
		cell.setCellStyle(style);
	}

	private static void autoSize(XSSFSheet sheet, int cols) {
		for (int i = 0; i < cols; i++)
			sheet.autoSizeColumn(i);
	}

	private static byte[] toBytes(XSSFWorkbook wb) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		wb.write(baos);
		wb.close();
		return baos.toByteArray();
	}

	private static String nvl(String v) {
		return v != null ? v : "-";
	}

	private static byte[] hexToBytes(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}
}