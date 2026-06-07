package com.example.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendancePunchDTO {
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    private String employeeName;

    private String department;

    @NotNull(message = "打卡时间不能为空")
    private LocalDateTime punchTime;

    private String punchType;
}
