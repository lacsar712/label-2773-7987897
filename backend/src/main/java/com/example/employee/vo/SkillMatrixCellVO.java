package com.example.employee.vo;

import lombok.Data;

@Data
public class SkillMatrixCellVO {
    private Long skillTagId;
    private String skillTagName;
    private String category;
    private Long employeeId;
    private String employeeName;
    private String department;
    private Integer proficiency;
    private Boolean isExpired;
}
