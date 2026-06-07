package com.example.employee.dto;

import com.example.employee.entity.performance.PerformanceGrade;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalibrationAdjustmentDTO {
    private Long meetingId;
    private Long evaluationId;
    private PerformanceGrade adjustedGrade;
    private BigDecimal adjustedScore;
    private Integer adjustedRank;
    private String adjustmentReason;
}
