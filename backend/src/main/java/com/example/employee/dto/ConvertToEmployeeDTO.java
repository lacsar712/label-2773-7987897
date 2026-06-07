package com.example.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConvertToEmployeeDTO {
    @NotNull(message = "候选人ID不能为空")
    private Long candidateId;

    @NotNull(message = "员工邮箱不能为空")
    private String employeeEmail;

    private String employeeDepartment;

    private String employeeRole;

    private Long operatorId;

    private String operatorName;
}
