package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("candidate")
public class Candidate {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    private String email;

    @TableField("applied_position")
    private String appliedPosition;

    private String department;

    @TableField("source_channel")
    private CandidateSource sourceChannel;

    @TableField("expected_salary_min")
    private BigDecimal expectedSalaryMin;

    @TableField("expected_salary_max")
    private BigDecimal expectedSalaryMax;

    @TableField("resume_attachment_id")
    private Long resumeAttachmentId;

    @TableField("resume_attachment_name")
    private String resumeAttachmentName;

    @TableField("referrer_id")
    private Long referrerId;

    @TableField("referrer_name")
    private String referrerName;

    private CandidateStage stage;

    @TableField("is_in_talent_pool")
    private Boolean isInTalentPool;

    @TableField("eliminate_reason")
    private String eliminateReason;

    @TableField("eliminate_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eliminateTime;

    @TableField("offer_salary")
    private BigDecimal offerSalary;

    @TableField("offer_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate offerStartDate;

    @TableField("offer_approval_status")
    private OfferApprovalStatus offerApprovalStatus;

    @TableField("offer_approver_id")
    private Long offerApproverId;

    @TableField("offer_approver_name")
    private String offerApproverName;

    @TableField("offer_approval_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime offerApprovalTime;

    @TableField("converted_employee_id")
    private Long convertedEmployeeId;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_by_name")
    private String createdByName;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @TableField("remark")
    private String remark;
}
