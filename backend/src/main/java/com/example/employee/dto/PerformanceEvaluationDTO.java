package com.example.employee.dto;

import com.example.employee.entity.performance.EvaluationCycle;
import com.example.employee.entity.performance.PerformanceGrade;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PerformanceEvaluationDTO {
    private Long batchId;
    private Long employeeId;
    private BigDecimal selfScore;
    private String selfComment;
    private BigDecimal managerScore;
    private PerformanceGrade finalGrade;
    private String managerComment;
    private String improvementPlan;
    private String hrComment;
    private String salaryAdjustmentSuggestion;
    private String potentialRating;
    private String performanceRating;
    private List<DimensionScoreDTO> dimensionScores;
}
