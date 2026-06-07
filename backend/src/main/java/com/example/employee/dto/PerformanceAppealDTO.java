package com.example.employee.dto;

import lombok.Data;

@Data
public class PerformanceAppealDTO {
    private Long evaluationId;
    private String appealReason;
    private String appealDetail;
}
