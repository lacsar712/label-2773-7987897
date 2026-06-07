package com.example.employee.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ScheduleEmployeeRowVO {
    private Long employeeId;
    private String employeeName;
    private String department;
    private String teamGroup;
    private Map<String, ScheduleCellVO> scheduleCells;
}
