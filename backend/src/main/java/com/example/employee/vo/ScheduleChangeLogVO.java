package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScheduleChangeLogVO {
    private Long id;
    private Long scheduleId;
    private Long employeeId;
    private String employeeName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;
    private String changeType;
    private Long oldShiftId;
    private String oldShiftCode;
    private String oldShiftName;
    private Long newShiftId;
    private String newShiftCode;
    private String newShiftName;
    private String changeReason;
    private Long operatorId;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changedAt;
}
