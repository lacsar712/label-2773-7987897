package com.example.employee.vo;

import com.example.employee.entity.performance.PerformanceGrade;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PeerBenchmarkVO {
    private BigDecimal employeeScore;
    private PerformanceGrade employeeGrade;
    private BigDecimal peerAverageScore;
    private BigDecimal peerMedianScore;
    private BigDecimal peerTop25Percentile;
    private BigDecimal peerBottom25Percentile;
    private Map<PerformanceGrade, Long> peerGradeDistribution;
    private Integer peerTotalCount;
    private Integer employeeRank;
    private List<BigDecimal> anonymousPeerScores;
}
