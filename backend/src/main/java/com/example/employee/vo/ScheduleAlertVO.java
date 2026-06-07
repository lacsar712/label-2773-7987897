package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleAlertVO {
    private Long id;
    private String alertType;
    private String severity;
    private String department;
    private Long employeeId;
    private String employeeName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alertDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alertStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alertEndDate;
    private String message;
    private String detail;
    private Boolean isResolved;
}
