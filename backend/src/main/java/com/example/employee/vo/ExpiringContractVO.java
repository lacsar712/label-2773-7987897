package com.example.employee.vo;

import com.example.employee.entity.contract.ContractType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpiringContractVO {
    private Long contractId;

    private String contractNo;

    private Long employeeId;

    private String employeeName;

    private String department;

    private ContractType contractType;

    private String contractTypeName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Long daysUntilExpiry;

    private String warningLevel;
}
