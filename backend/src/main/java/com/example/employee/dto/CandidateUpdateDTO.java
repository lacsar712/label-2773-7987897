package com.example.employee.dto;

import com.example.employee.entity.recruitment.CandidateSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateUpdateDTO {
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
}
