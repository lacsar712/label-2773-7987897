package com.example.employee.vo;

import lombok.Data;

@Data
public class StorageQuotaVO {
    private Long employeeId;

    private String employeeName;

    private Long totalQuotaBytes;

    private String totalQuotaDisplay;

    private Long usedBytes;

    private String usedDisplay;

    private Long remainingBytes;

    private String remainingDisplay;

    private Double usagePercent;

    private Long maxSingleFileBytes;

    private String maxSingleFileDisplay;
}
