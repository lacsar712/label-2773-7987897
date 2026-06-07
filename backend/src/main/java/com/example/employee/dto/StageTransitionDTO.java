package com.example.employee.dto;

import com.example.employee.entity.recruitment.CandidateStage;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageTransitionDTO {
    @NotNull(message = "目标阶段不能为空")
    private CandidateStage targetStage;

    private String transitionReason;

    private String remark;

    private Long operatorId;

    private String operatorName;
}
