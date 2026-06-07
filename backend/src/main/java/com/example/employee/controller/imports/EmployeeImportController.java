package com.example.employee.controller.imports;

import com.example.employee.common.Result;
import com.example.employee.dto.EmployeeImportDTO;
import com.example.employee.entity.imports.ImportMode;
import com.example.employee.service.imports.EmployeeFailedExportService;
import com.example.employee.service.imports.EmployeeImportService;
import com.example.employee.service.imports.EmployeeImportTaskService;
import com.example.employee.service.imports.EmployeeTemplateService;
import com.example.employee.vo.ImportProgressVO;
import com.example.employee.vo.ImportReportVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/employees/import")
@CrossOrigin(origins = "*")
public class EmployeeImportController {

    @Autowired
    private EmployeeTemplateService templateService;

    @Autowired
    private EmployeeImportService importService;

    @Autowired
    private EmployeeImportTaskService taskService;

    @Autowired
    private EmployeeFailedExportService failedExportService;

    @GetMapping("/template/excel")
    public void downloadExcelTemplate(HttpServletResponse response) throws Exception {
        byte[] data = templateService.generateExcelTemplate();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = URLEncoder.encode("员工导入模板.xlsx", StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    @GetMapping("/template/csv")
    public void downloadCsvTemplate(HttpServletResponse response) throws Exception {
        byte[] data = templateService.generateCsvTemplate();
        response.setContentType("text/csv; charset=UTF-8");
        String filename = URLEncoder.encode("员工导入模板.csv", StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadAndImport(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "importMode", defaultValue = "INSERT_ONLY") ImportMode importMode,
            @RequestParam(value = "operator", required = false) String operator) {
        try {
            String taskId = importService.startImport(file, importMode, operator);
            return Result.success(taskId);
        } catch (Exception e) {
            return Result.error("导入启动失败: " + e.getMessage());
        }
    }

    @GetMapping("/progress/{taskId}")
    public Result<ImportProgressVO> getProgress(@PathVariable String taskId) {
        ImportProgressVO progress = taskService.getProgress(taskId);
        if (progress == null) {
            return Result.error("任务不存在: " + taskId);
        }
        return Result.success(progress);
    }

    @GetMapping("/report/{taskId}")
    public Result<ImportReportVO> getReport(@PathVariable String taskId) {
        ImportReportVO report = taskService.getReport(taskId);
        if (report == null) {
            return Result.error("任务不存在: " + taskId);
        }
        return Result.success(report);
    }

    @GetMapping("/failed/{taskId}/excel")
    public void downloadFailedExcel(@PathVariable String taskId, HttpServletResponse response) throws Exception {
        byte[] data = failedExportService.exportFailedExcel(taskId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = URLEncoder.encode("导入失败记录_" + taskId + ".xlsx", StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    @GetMapping("/failed/{taskId}/csv")
    public void downloadFailedCsv(@PathVariable String taskId, HttpServletResponse response) throws Exception {
        byte[] data = failedExportService.exportFailedCsv(taskId);
        response.setContentType("text/csv; charset=UTF-8");
        String filename = URLEncoder.encode("导入失败记录_" + taskId + ".csv", StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
