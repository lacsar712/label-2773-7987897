package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleCellVO {
    private Long scheduleId;
    private LocalDate scheduleDate;
    private Long shiftId;
    private String shiftCode;
    private String shiftName;
    private String color;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private Boolean isCrossDay;
    private String status;
    private String remark;
    private Boolean hasAlert;
}
