package com.example.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EliminateCandidateDTO {
    @NotNull(message = "候选人ID不能为空")
    private Long candidateId;

    @NotBlank(message = "淘汰原因不能为空")
    private String eliminateReason;

    private Boolean addToTalentPool = true;

    private Long operatorId;

    private String operatorName;
}
