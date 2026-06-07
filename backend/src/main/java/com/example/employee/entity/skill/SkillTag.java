package com.example.employee.entity.skill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("skill_tag")
public class SkillTag {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @TableField("tag_name")
    private String tagName;

    @NotNull(message = "技能分类不能为空")
    @TableField("category")
    private SkillCategory category;

    @TableField("description")
    private String description;

    @TableField("heat_weight")
    private BigDecimal heatWeight;

    @TableField("validation_cycle_days")
    private Integer validationCycleDays;

    @TableField("is_active")
    private Boolean isActive;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
