package com.example.employee.vo;

import com.example.employee.entity.recruitment.InterviewRound;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InterviewRecordVO {
    private Long id;

    private Long candidateId;

    private String candidateName;

    private InterviewRound interviewRound;

    private String interviewRoundName;

    private Long interviewerId;

    private String interviewerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;

    private BigDecimal score;

    private String evaluation;

    private Boolean isPassed;

    private Long createdBy;

    private String createdByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String remark;
}
