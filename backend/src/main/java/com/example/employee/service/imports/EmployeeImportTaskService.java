package com.example.employee.service.imports;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.imports.EmployeeImportTask;
import com.example.employee.entity.imports.ImportTaskStatus;
import com.example.employee.mapper.imports.EmployeeImportTaskMapper;
import com.example.employee.vo.FailedRowVO;
import com.example.employee.vo.ImportProgressVO;
import com.example.employee.vo.ImportReportVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeImportTaskService extends ServiceImpl<EmployeeImportTaskMapper, EmployeeImportTask> {

    @Autowired
    private ObjectMapper objectMapper;

    public String createTask(String fileName, String fileType,
                             com.example.employee.entity.imports.ImportMode importMode,
                             String operator) {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        EmployeeImportTask task = new EmployeeImportTask();
        task.setTaskId(taskId);
        task.setFileName(fileName);
        task.setFileType(fileType);
        task.setImportMode(importMode);
        task.setStatus(ImportTaskStatus.PENDING);
        task.setTotalRows(0);
        task.setProcessedRows(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        task.setOperator(operator);
        task.setCreatedAt(java.time.LocalDateTime.now());
        this.save(task);
        return taskId;
    }

    public void updateProgress(String taskId, int totalRows, int processedRows,
                               int successCount, int failCount) {
        EmployeeImportTask task = getByTaskId(taskId);
        if (task != null) {
            task.setStatus(ImportTaskStatus.PROCESSING);
            task.setTotalRows(totalRows);
            task.setProcessedRows(processedRows);
            task.setSuccessCount(successCount);
            task.setFailCount(failCount);
            this.updateById(task);
        }
    }

    public void completeTask(String taskId, int successCount, int failCount,
                             List<FailedRowVO> failedRows) {
        EmployeeImportTask task = getByTaskId(taskId);
        if (task != null) {
            task.setStatus(ImportTaskStatus.COMPLETED);
            task.setSuccessCount(successCount);
            task.setFailCount(failCount);
            task.setProcessedRows(task.getTotalRows());
            task.setCompletedAt(java.time.LocalDateTime.now());
            try {
                task.setFailedRowsJson(objectMapper.writeValueAsString(failedRows));
            } catch (JsonProcessingException e) {
                task.setFailedRowsJson("[]");
            }
            this.updateById(task);
        }
    }

    public void failTask(String taskId, String errorMessage) {
        EmployeeImportTask task = getByTaskId(taskId);
        if (task != null) {
            task.setStatus(ImportTaskStatus.FAILED);
            task.setErrorMessage(errorMessage);
            task.setCompletedAt(java.time.LocalDateTime.now());
            this.updateById(task);
        }
    }

    public EmployeeImportTask getByTaskId(String taskId) {
        QueryWrapper<EmployeeImportTask> qw = new QueryWrapper<>();
        qw.eq("task_id", taskId);
        return this.getOne(qw);
    }

    public ImportProgressVO getProgress(String taskId) {
        EmployeeImportTask task = getByTaskId(taskId);
        if (task == null) {
            return null;
        }
        ImportProgressVO vo = new ImportProgressVO();
        vo.setTaskId(task.getTaskId());
        vo.setStatus(task.getStatus());
        vo.setTotalRows(task.getTotalRows() == null ? 0 : task.getTotalRows());
        vo.setProcessedRows(task.getProcessedRows() == null ? 0 : task.getProcessedRows());
        vo.setSuccessCount(task.getSuccessCount() == null ? 0 : task.getSuccessCount());
        vo.setFailCount(task.getFailCount() == null ? 0 : task.getFailCount());
        if (vo.getTotalRows() > 0) {
            vo.setProgressPercent((int) (vo.getProcessedRows() * 100.0 / vo.getTotalRows()));
        }
        return vo;
    }

    public ImportReportVO getReport(String taskId) {
        EmployeeImportTask task = getByTaskId(taskId);
        if (task == null) {
            return null;
        }
        ImportReportVO vo = new ImportReportVO();
        vo.setTaskId(task.getTaskId());
        vo.setStatus(task.getStatus());
        vo.setTotalRows(task.getTotalRows() == null ? 0 : task.getTotalRows());
        vo.setSuccessCount(task.getSuccessCount() == null ? 0 : task.getSuccessCount());
        vo.setFailCount(task.getFailCount() == null ? 0 : task.getFailCount());
        if (task.getFailedRowsJson() != null && !task.getFailedRowsJson().isEmpty()) {
            try {
                vo.setFailedRows(objectMapper.readValue(task.getFailedRowsJson(),
                        new TypeReference<List<FailedRowVO>>() {}));
            } catch (JsonProcessingException e) {
                vo.setFailedRows(new ArrayList<>());
            }
        }
        return vo;
    }

    public List<FailedRowVO> getFailedRows(String taskId) {
        ImportReportVO report = getReport(taskId);
        return report == null ? new ArrayList<>() : report.getFailedRows();
    }
}
