package com.example.employee.service.imports;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.employee.entity.Employee;
import com.example.employee.entity.imports.ImportMode;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.audit.AuditLogService;
import com.example.employee.vo.FailedRowVO;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmployeeImportService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeImportService.class);
    private static final int BATCH_SIZE = 100;
    private static final int PROGRESS_UPDATE_INTERVAL = 50;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeImportValidator validator;

    @Autowired
    private EmployeeImportTaskService taskService;

    @Autowired
    private AuditLogService auditLogService;

    public String startImport(MultipartFile file, ImportMode importMode, String operator) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String fileType = fileName.toLowerCase().endsWith(".csv") ? "CSV" :
                (fileName.toLowerCase().endsWith(".xlsx") ? "XLSX" :
                        (fileName.toLowerCase().endsWith(".xls") ? "XLS" : null));
        if (fileType == null) {
            throw new IllegalArgumentException("仅支持 CSV, XLSX, XLS 格式文件");
        }

        String taskId = taskService.createTask(fileName, fileType, importMode, operator);

        processFileAsync(taskId, file.getInputStream(), fileType, importMode, operator);

        return taskId;
    }

    @Async("importTaskExecutor")
    public void processFileAsync(String taskId, InputStream inputStream, String fileType,
                                  ImportMode importMode, String operator) {
        try {
            List<Map<String, String>> allRows = parseFile(inputStream, fileType);
            int totalRows = allRows.size();
            taskService.updateProgress(taskId, totalRows, 0, 0, 0);

            processRows(taskId, allRows, importMode, operator);
        } catch (Exception e) {
            log.error("Import task {} failed", taskId, e);
            taskService.failTask(taskId, e.getMessage());
            auditLogService.logImport(operator, taskId, "导入失败: " + e.getMessage());
        }
    }

    private List<Map<String, String>> parseFile(InputStream inputStream, String fileType) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        if ("CSV".equalsIgnoreCase(fileType)) {
            parseCsv(inputStream, rows);
        } else {
            parseExcel(inputStream, rows);
        }
        return rows;
    }

    private void parseCsv(InputStream inputStream, List<Map<String, String>> rows) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String[]> all = reader.readAll();
            if (all.isEmpty()) {
                return;
            }
            String[] headers = all.get(0);
            for (int i = 1; i < all.size(); i++) {
                String[] row = all.get(i);
                if (isEmptyRow(row)) {
                    continue;
                }
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    String header = headers[j] == null ? "" : headers[j].trim();
                    String value = j < row.length ? (row[j] == null ? "" : row[j].trim()) : "";
                    rowData.put(header, value);
                }
                rows.add(rowData);
            }
        }
    }

    private void parseExcel(InputStream inputStream, List<Map<String, String>> rows) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                return;
            }
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell == null ? "" : getCellStringValue(cell).trim());
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isEmptyRow(row, headers.size())) {
                    continue;
                }
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = cell == null ? "" : getCellStringValue(cell).trim();
                    rowData.put(headers.get(j), value);
                }
                rows.add(rowData);
            }
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e2) {
                        return "";
                    }
                }
            default:
                return "";
        }
    }

    private boolean isEmptyRow(String[] row) {
        if (row == null) return true;
        for (String s : row) {
            if (s != null && !s.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmptyRow(Row row, int colCount) {
        if (row == null) return true;
        for (int j = 0; j < colCount; j++) {
            Cell cell = row.getCell(j);
            String val = cell == null ? "" : getCellStringValue(cell).trim();
            if (!val.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    protected void processRows(String taskId, List<Map<String, String>> allRows,
                                ImportMode importMode, String operator) {
        int totalRows = allRows.size();
        Set<String> fileEmails = new HashSet<>();
        List<Employee> toInsertBatch = new ArrayList<>();
        List<Employee> toUpdateBatch = new ArrayList<>();
        List<FailedRowVO> failedRows = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        int processedRows = 0;

        for (int i = 0; i < totalRows; i++) {
            Map<String, String> rowData = allRows.get(i);
            int originalRowNumber = i + 2;
            processedRows++;

            String error = importMode == ImportMode.UPSERT
                    ? validator.validateForUpsert(rowData, fileEmails)
                    : validator.validate(rowData, fileEmails);

            if (error != null) {
                failCount++;
                failedRows.add(new FailedRowVO(originalRowNumber, error, rowData));
            } else {
                Employee emp = validator.toEmployee(rowData);
                if (importMode == ImportMode.UPSERT) {
                    Employee existing = findByEmail(emp.getEmail());
                    if (existing != null) {
                        emp.setId(existing.getId());
                        toUpdateBatch.add(emp);
                    } else {
                        toInsertBatch.add(emp);
                    }
                } else {
                    toInsertBatch.add(emp);
                }
                successCount++;
            }

            if (toInsertBatch.size() >= BATCH_SIZE) {
                employeeService.saveBatch(toInsertBatch);
                toInsertBatch.clear();
            }
            if (toUpdateBatch.size() >= BATCH_SIZE) {
                employeeService.updateBatchById(toUpdateBatch);
                toUpdateBatch.clear();
            }

            if (processedRows % PROGRESS_UPDATE_INTERVAL == 0 || processedRows == totalRows) {
                taskService.updateProgress(taskId, totalRows, processedRows, successCount, failCount);
            }
        }

        if (!toInsertBatch.isEmpty()) {
            employeeService.saveBatch(toInsertBatch);
        }
        if (!toUpdateBatch.isEmpty()) {
            employeeService.updateBatchById(toUpdateBatch);
        }

        taskService.completeTask(taskId, successCount, failCount, failedRows);

        String auditDetail = String.format("导入完成: 总数=%d, 成功=%d, 失败=%d, 模式=%s",
                totalRows, successCount, failCount, importMode);
        auditLogService.logImport(operator, taskId, auditDetail);
    }

    private Employee findByEmail(String email) {
        QueryWrapper<Employee> qw = new QueryWrapper<>();
        qw.eq("email", email);
        return employeeService.getOne(qw);
    }
}
