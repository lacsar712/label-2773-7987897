package com.example.employee.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DimensionScoreDTO {
    private Long id;
    private String dimensionName;
    private String dimensionCode;
    private BigDecimal weight;
    private BigDecimal selfScore;
    private BigDecimal managerScore;
    private String selfComment;
    private String managerComment;
}
