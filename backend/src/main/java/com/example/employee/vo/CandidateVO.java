package com.example.employee.vo;

import com.example.employee.entity.recruitment.CandidateSource;
import com.example.employee.entity.recruitment.CandidateStage;
import com.example.employee.entity.recruitment.OfferApprovalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CandidateVO {
    private Long id;

    private String name;

    private String phone;

    private String email;

    private String appliedPosition;

    private String department;

    private CandidateSource sourceChannel;

    private String sourceChannelName;

    private BigDecimal expectedSalaryMin;

    private BigDecimal expectedSalaryMax;

    private Long resumeAttachmentId;

    private String resumeAttachmentName;

    private Long referrerId;

    private String referrerName;

    private CandidateStage stage;

    private String stageName;

    private Boolean isInTalentPool;

    private String eliminateReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eliminateTime;

    private BigDecimal offerSalary;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate offerStartDate;

    private OfferApprovalStatus offerApprovalStatus;

    private String offerApprovalStatusName;

    private Long offerApproverId;

    private String offerApproverName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime offerApprovalTime;

    private Long convertedEmployeeId;

    private Long createdBy;

    private String createdByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String remark;

    private List<InterviewRecordVO> interviewRecords;

    private List<StageTransitionLogVO> transitionLogs;
}
