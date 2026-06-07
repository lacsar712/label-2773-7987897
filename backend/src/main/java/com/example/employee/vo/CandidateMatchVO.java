package com.example.employee.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CandidateMatchVO {
    private Long employeeId;
    private String employeeName;
    private String department;
    private String role;
    private BigDecimal matchScore;
    private List<SkillMatchDetail> matchedSkills;

    @Data
    public static class SkillMatchDetail {
        private Long skillTagId;
        private String skillTagName;
        private Integer requiredProficiency;
        private Integer actualProficiency;
        private Boolean isExpired;
    }
}
