package com.example.employee.vo;

import com.example.employee.entity.performance.PerformanceGrade;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PerformanceHistoryVO {
    private Long batchId;
    private String batchName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal score;
    private PerformanceGrade grade;
    private Integer rankInDept;
    private Integer deptTotalCount;
}
