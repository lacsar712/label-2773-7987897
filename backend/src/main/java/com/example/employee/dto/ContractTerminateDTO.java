package com.example.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractTerminateDTO {
    @NotBlank(message = "终止原因不能为空")
    private String terminationReason;

    @NotNull(message = "终止日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminationDate;

    private Long operatorId;

    private String operatorName;
}
