package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("interview_record")
public class InterviewRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("candidate_id")
    private Long candidateId;

    @TableField("candidate_name")
    private String candidateName;

    @TableField("interview_round")
    private InterviewRound interviewRound;

    @TableField("interviewer_id")
    private Long interviewerId;

    @TableField("interviewer_name")
    private String interviewerName;

    @TableField("interview_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;

    private BigDecimal score;

    @TableField("evaluation")
    private String evaluation;

    @TableField("is_passed")
    private Boolean isPassed;

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
