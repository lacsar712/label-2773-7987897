package com.example.employee.entity.contract;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("employee_contract")
public class EmployeeContract {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("contract_no")
    private String contractNo;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    private String department;

    @TableField("contract_type")
    private ContractType contractType;

    @TableField("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @TableField("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @TableField("probation_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationStartDate;

    @TableField("probation_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate probationEndDate;

    @TableField("probation_salary_ratio")
    private BigDecimal probationSalaryRatio;

    @TableField("contract_status")
    private ContractStatus contractStatus;

    @TableField("sign_status")
    private SignStatus signStatus;

    @TableField("signed_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate signedDate;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("previous_contract_id")
    private Long previousContractId;

    @TableField("termination_reason")
    private String terminationReason;

    @TableField("termination_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminationDate;

    @TableField("termination_operator_id")
    private Long terminationOperatorId;

    @TableField("termination_operator_name")
    private String terminationOperatorName;

    @TableField("is_offboarding_triggered")
    private Boolean isOffboardingTriggered;

    @TableField("warning_30d_sent")
    private Boolean warning30dSent;

    @TableField("warning_15d_sent")
    private Boolean warning15dSent;

    @TableField("warning_7d_sent")
    private Boolean warning7dSent;

    @TableField("remark")
    private String remark;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_by_name")
    private String createdByName;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
