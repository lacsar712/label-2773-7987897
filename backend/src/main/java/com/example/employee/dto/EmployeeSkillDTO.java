package com.example.employee.dto;

import com.example.employee.entity.skill.ProficiencyLevel;
import com.example.employee.entity.skill.SkillCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeSkillDTO {
    private Long id;

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotNull(message = "技能标签ID不能为空")
    private Long skillTagId;

    private String skillTagName;

    private SkillCategory category;

    @NotNull(message = "熟练度不能为空")
    private ProficiencyLevel proficiency;

    private LocalDate lastVerifiedDate;

    private String evidence;

    private String changeReason;

    private Long operatorId;

    private String operatorName;
}
