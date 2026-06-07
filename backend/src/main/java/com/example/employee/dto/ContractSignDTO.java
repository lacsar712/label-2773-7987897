package com.example.employee.dto;

import com.example.employee.entity.contract.SignStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractSignDTO {
    @NotNull(message = "签署状态不能为空")
    private SignStatus signStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate signedDate;

    private String rejectReason;
}
