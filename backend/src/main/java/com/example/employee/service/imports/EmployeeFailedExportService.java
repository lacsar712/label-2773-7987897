package com.example.employee.service.imports;

import com.example.employee.vo.FailedRowVO;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.example.employee.service.imports.EmployeeImportValidator.*;

@Service
public class EmployeeFailedExportService {

    private static final String FAIL_REASON_COL = "失败原因";
    private static final String ROW_NUMBER_COL = "原始行号";

    @Autowired
    private EmployeeImportTaskService taskService;

    public byte[] exportFailedExcel(String taskId) throws Exception {
        List<FailedRowVO> failedRows = taskService.getFailedRows(taskId);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("失败记录");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = buildHeaders();
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 25 * 256);
            }

            for (int i = 0; i < failedRows.size(); i++) {
                FailedRowVO fr = failedRows.get(i);
                Row row = sheet.createRow(i + 1);
                Map<String, String> data = fr.getRowData();
                int colIdx = 0;
                row.createCell(colIdx++).setCellValue(fr.getRowNumber());
                for (String col : ALL_COLUMNS) {
                    row.createCell(colIdx++).setCellValue(data.getOrDefault(col, ""));
                }
                row.createCell(colIdx).setCellValue(fr.getReason());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportFailedCsv(String taskId) throws Exception {
        List<FailedRowVO> failedRows = taskService.getFailedRows(taskId);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(osw)) {

            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            osw.flush();

            csvWriter.writeNext(buildHeaders());

            for (FailedRowVO fr : failedRows) {
                String[] line = buildRow(fr);
                csvWriter.writeNext(line);
            }

            csvWriter.flush();
            return baos.toByteArray();
        }
    }

    private String[] buildHeaders() {
        String[] headers = new String[ALL_COLUMNS.length + 2];
        headers[0] = ROW_NUMBER_COL;
        System.arraycopy(ALL_COLUMNS, 0, headers, 1, ALL_COLUMNS.length);
        headers[headers.length - 1] = FAIL_REASON_COL;
        return headers;
    }

    private String[] buildRow(FailedRowVO fr) {
        String[] row = new String[ALL_COLUMNS.length + 2];
        Map<String, String> data = fr.getRowData();
        row[0] = String.valueOf(fr.getRowNumber());
        for (int i = 0; i < ALL_COLUMNS.length; i++) {
            row[i + 1] = data.getOrDefault(ALL_COLUMNS[i], "");
        }
        row[row.length - 1] = fr.getReason();
        return row;
    }
}
