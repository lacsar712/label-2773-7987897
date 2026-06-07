package com.example.employee.dto;

import com.example.employee.entity.recruitment.CandidateSource;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateCreateDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    private String email;

    private String appliedPosition;

    private String department;

    private CandidateSource sourceChannel;

    private BigDecimal expectedSalaryMin;

    private BigDecimal expectedSalaryMax;

    private Long resumeAttachmentId;

    private String resumeAttachmentName;

    private Long referrerId;

    private String referrerName;

    private String remark;

    private Long createdBy;

    private String createdByName;
}
