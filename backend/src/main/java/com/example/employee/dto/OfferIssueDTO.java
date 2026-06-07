package com.example.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OfferIssueDTO {
    @NotNull(message = "候选人ID不能为空")
    private Long candidateId;

    @NotNull(message = "Offer薪资不能为空")
    private BigDecimal offerSalary;

    @NotNull(message = "入职日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate offerStartDate;

    private Long operatorId;

    private String operatorName;
}
