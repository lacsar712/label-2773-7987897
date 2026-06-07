package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AttendanceCompareVO {
    private Long attendanceId;
    private Long employeeId;
    private String employeeName;
    private String department;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;
    private Long scheduleId;
    private Long shiftId;
    private String shiftCode;
    private String shiftName;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime scheduledStartTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime scheduledEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime punchInTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime punchOutTime;
    private BigDecimal workHours;
    private Boolean isLate;
    private Integer lateMinutes;
    private Boolean isEarlyLeave;
    private Integer earlyLeaveMinutes;
    private Boolean isAbsent;
    private Boolean isAbnormal;
    private String abnormalType;
    private String abnormalReason;
}
