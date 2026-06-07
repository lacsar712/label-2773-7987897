package com.example.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SkillMergeDTO {
    @NotNull(message = "目标标签ID不能为空")
    private Long targetTagId;

    @NotNull(message = "待合并标签ID列表不能为空")
    private List<Long> sourceTagIds;
}
