package com.example.employee.dto;

import com.example.employee.entity.skill.ProficiencyLevel;
import com.example.employee.entity.skill.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkillTagDTO {
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String tagName;

    @NotNull(message = "技能分类不能为空")
    private SkillCategory category;

    private String description;

    private BigDecimal heatWeight;

    private Integer validationCycleDays;

    private Boolean isActive;
}
