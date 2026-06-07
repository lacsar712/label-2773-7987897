package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleBatchUpdateDTO {
    @NotBlank(message = "变更原因不能为空")
    private String changeReason;

    private String department;

    private String teamGroup;

    private List<Long> employeeIds;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    private List<LocalDate> dates;

    @NotNull(message = "班次ID不能为空")
    private Long shiftId;

    private String shiftCode;

    private String shiftName;
}
