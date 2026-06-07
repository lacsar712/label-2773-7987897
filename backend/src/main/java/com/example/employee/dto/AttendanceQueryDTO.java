package com.example.employee.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceQueryDTO {
    private String department;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isAbnormal;
    private String abnormalType;
}
