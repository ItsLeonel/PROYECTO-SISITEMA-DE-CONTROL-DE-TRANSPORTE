package Logica.Servicios;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportarService {

    public void exportarExcel(JTable tabla, String tituloHoja, File archivo) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) { // .xlsx format
            Sheet sheet = workbook.createSheet(tituloHoja);

            // --- ESTILOS ---

            // Estilo Header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Estilo Datos
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // --- HEADER ---
            Row headerRow = sheet.createRow(0);
            TableModel model = tabla.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }

            // --- DATOS ---
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Cell cell = row.createCell(c);
                    Object valor = model.getValueAt(r, c);

                    if (valor != null) {
                        cell.setCellValue(valor.toString());
                    } else {
                        cell.setCellValue("");
                    }
                    cell.setCellStyle(dataStyle);
                }
            }

            // --- AUTO AJUSTAR COLUMNAS ---
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // --- GUARDAR ---
            try (FileOutputStream out = new FileOutputStream(archivo)) {
                workbook.write(out);
            }
        }
    }
}
