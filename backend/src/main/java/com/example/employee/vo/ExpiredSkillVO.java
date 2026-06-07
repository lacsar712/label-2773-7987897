package com.example.employee.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpiredSkillVO {
    private Long employeeSkillId;
    private Long employeeId;
    private String employeeName;
    private String department;
    private Long skillTagId;
    private String skillTagName;
    private Integer proficiency;
    private LocalDate lastVerifiedDate;
    private Long daysOverdue;
}
