package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ShiftDefinitionDTO {
    private Long id;

    @NotBlank(message = "班次编码不能为空")
    private String shiftCode;

    @NotBlank(message = "班次名称不能为空")
    private String shiftName;

    @NotBlank(message = "部门不能为空")
    private String department;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    private Boolean isCrossDay;

    private String color;

    private Integer sortOrder;

    private String description;
}
