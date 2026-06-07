package com.example.employee.vo;

import com.example.employee.entity.contract.ContractStatus;
import com.example.employee.entity.contract.ContractType;
import com.example.employee.entity.contract.SignStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractVO {
    private Long id;

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationEndDate;

    private BigDecimal probationSalaryRatio;

    private ContractStatus contractStatus;

    private String contractStatusName;

    private SignStatus signStatus;

    private String signStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate signedDate;

    private String rejectReason;

    private Long previousContractId;

    private String previousContractNo;

    private String terminationReason;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminationDate;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
