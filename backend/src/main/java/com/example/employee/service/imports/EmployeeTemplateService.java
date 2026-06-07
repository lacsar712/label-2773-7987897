package com.example.employee.service.imports;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static com.example.employee.service.imports.EmployeeImportValidator.*;

@Service
public class EmployeeTemplateService {

    public byte[] generateExcelTemplate() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("员工导入模板");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle descStyle = workbook.createCellStyle();
            descStyle.setWrapText(true);

            Row headerRow = sheet.createRow(0);
            Row descRow = sheet.createRow(1);
            Row sampleRow = sheet.createRow(2);

            for (int i = 0; i < ALL_COLUMNS.length; i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(ALL_COLUMNS[i]);
                headerCell.setCellStyle(headerStyle);

                Cell descCell = descRow.createCell(i);
                descCell.setCellValue(COLUMN_DESCRIPTIONS[i]);
                descCell.setCellStyle(descStyle);

                Cell sampleCell = sampleRow.createCell(i);
                sampleCell.setCellValue(SAMPLE_DATA[i]);

                sheet.setColumnWidth(i, 25 * 256);
            }

            sheet.createFreezePane(0, 3);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateCsvTemplate() throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(osw)) {

            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            osw.flush();

            csvWriter.writeNext(ALL_COLUMNS);
            csvWriter.writeNext(COLUMN_DESCRIPTIONS);
            csvWriter.writeNext(SAMPLE_DATA);

            csvWriter.flush();
            return baos.toByteArray();
        }
    }
}
