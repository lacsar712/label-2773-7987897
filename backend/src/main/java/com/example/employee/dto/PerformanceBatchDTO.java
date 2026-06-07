package com.example.employee.dto;

import com.example.employee.entity.performance.EvaluationCycle;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PerformanceBatchDTO {
    private String batchName;
    private EvaluationCycle cycleType;
    private Integer cycleYear;
    private Integer cycleQuarter;
    private String department;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime selfEvalDeadline;
    private LocalDateTime managerReviewDeadline;
    private LocalDateTime hrReviewDeadline;
    private String description;
    private List<Long> employeeIds;
}
