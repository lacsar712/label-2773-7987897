package com.example.employee.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class ScheduleWeekMatrixVO {
    private String scheduleWeek;
    private String department;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private List<LocalDate> weekDates;
    private List<ScheduleEmployeeRowVO> employeeRows;
    private List<ScheduleAlertVO> alerts;
    private String status;
}
