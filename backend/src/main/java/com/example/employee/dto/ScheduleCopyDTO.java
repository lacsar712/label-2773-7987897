package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleCopyDTO {
    @NotBlank(message = "变更原因不能为空")
    private String changeReason;

    @NotBlank(message = "源周不能为空")
    private String sourceWeek;

    @NotBlank(message = "目标周不能为空")
    private String targetWeek;

    private String department;

    private String teamGroup;
}
