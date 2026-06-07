package com.example.employee.entity.skill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("employee_skill")
public class EmployeeSkill {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "员工ID不能为空")
    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("department")
    private String department;

    @NotNull(message = "技能标签ID不能为空")
    @TableField("skill_tag_id")
    private Long skillTagId;

    @TableField("skill_tag_name")
    private String skillTagName;

    @TableField("category")
    private SkillCategory category;

    @NotNull(message = "熟练度不能为空")
    @TableField("proficiency")
    private ProficiencyLevel proficiency;

    @TableField("last_verified_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastVerifiedDate;

    @TableField("is_expired")
    private Boolean isExpired;

    @TableField("evidence")
    private String evidence;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
