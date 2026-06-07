package com.example.employee.vo;

import com.example.employee.entity.performance.PerformanceGrade;
import lombok.Data;

import java.util.Map;

@Data
public class GradeDistributionVO {
    private String department;
    private Long batchId;
    private Map<PerformanceGrade, Long> gradeCounts;
    private Map<PerformanceGrade, Double> gradePercentages;
    private Integer totalCount;
}
