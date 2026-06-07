package com.example.employee.dto;

import com.example.employee.entity.contract.ContractStatus;
import com.example.employee.entity.contract.ContractType;
import com.example.employee.entity.contract.SignStatus;
import lombok.Data;

@Data
public class ContractQueryDTO {
    private Long employeeId;

    private String department;

    private ContractType contractType;

    private ContractStatus contractStatus;

    private SignStatus signStatus;

    private String keyword;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
