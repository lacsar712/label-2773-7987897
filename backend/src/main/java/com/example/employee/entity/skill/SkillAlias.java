package com.example.employee.entity.skill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("skill_alias")
public class SkillAlias {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "别名不能为空")
    @TableField("alias_name")
    private String aliasName;

    @NotNull(message = "主标签ID不能为空")
    @TableField("primary_tag_id")
    private Long primaryTagId;

    @TableField("primary_tag_name")
    private String primaryTagName;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
