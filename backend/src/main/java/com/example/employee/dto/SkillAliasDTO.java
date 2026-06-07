package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SkillAliasDTO {
    private Long id;

    @NotBlank(message = "别名不能为空")
    private String aliasName;

    @NotNull(message = "主标签ID不能为空")
    private Long primaryTagId;
}
