package com.example.employee.vo;

import com.example.employee.entity.imports.ImportTaskStatus;
import lombok.Data;

@Data
public class ImportProgressVO {
    private String taskId;
    private ImportTaskStatus status;
    private int totalRows;
    private int processedRows;
    private int successCount;
    private int failCount;
    private int progressPercent;
}
