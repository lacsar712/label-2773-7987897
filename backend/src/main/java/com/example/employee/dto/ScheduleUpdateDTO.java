package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleUpdateDTO {
    @NotBlank(message = "变更原因不能为空")
    private String changeReason;

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    private String employeeName;

    @NotNull(message = "排班日期不能为空")
    private LocalDate scheduleDate;

    @NotNull(message = "班次ID不能为空")
    private Long shiftId;

    private String shiftCode;

    private String shiftName;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean isCrossDay;

    private String remark;
}
