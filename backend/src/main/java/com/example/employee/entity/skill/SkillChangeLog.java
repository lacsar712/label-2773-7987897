package com.example.employee.entity.skill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("skill_change_log")
public class SkillChangeLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("skill_tag_id")
    private Long skillTagId;

    @TableField("skill_tag_name")
    private String skillTagName;

    @TableField("change_type")
    private String changeType;

    @TableField("old_proficiency")
    private Integer oldProficiency;

    @TableField("new_proficiency")
    private Integer newProficiency;

    @TableField("old_last_verified_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate oldLastVerifiedDate;

    @TableField("new_last_verified_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate newLastVerifiedDate;

    @TableField("change_reason")
    private String changeReason;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
