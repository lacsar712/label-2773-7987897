package com.example.employee.dto;

import com.example.employee.entity.contract.ContractType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractRenewDTO {
    @NotNull(message = "合同类型不能为空")
    private ContractType contractType;

    @NotNull(message = "新合同开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationEndDate;

    private BigDecimal probationSalaryRatio;

    private String remark;

    private Long createdBy;

    private String createdByName;
}
