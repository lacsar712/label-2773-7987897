package com.example.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleQueryDTO {
    private String department;
    private String teamGroup;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduleWeek;
    private String status;
}
