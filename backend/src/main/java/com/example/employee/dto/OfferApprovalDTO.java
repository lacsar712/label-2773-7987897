package com.example.employee.dto;

import com.example.employee.entity.recruitment.OfferApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OfferApprovalDTO {
    @NotNull(message = "候选人ID不能为空")
    private Long candidateId;

    @NotNull(message = "审批结果不能为空")
    private OfferApprovalStatus approvalStatus;

    private String approvalComment;

    private Long approverId;

    private String approverName;
}
