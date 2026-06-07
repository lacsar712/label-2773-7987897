package com.example.employee.vo;

import com.example.employee.entity.imports.ImportTaskStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportReportVO {
    private String taskId;
    private ImportTaskStatus status;
    private int totalRows;
    private int successCount;
    private int failCount;
    private List<FailedRowVO> failedRows = new ArrayList<>();
}
