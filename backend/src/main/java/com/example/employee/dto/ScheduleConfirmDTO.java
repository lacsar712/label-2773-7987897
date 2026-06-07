package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleConfirmDTO {
    private List<Long> scheduleIds;

    private String scheduleWeek;

    private String department;

    @NotBlank(message = "确认备注不能为空")
    private String remark;
}
