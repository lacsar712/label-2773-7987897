package com.example.employee.dto;

import com.example.employee.entity.recruitment.InterviewRound;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InterviewRecordCreateDTO {
    @NotNull(message = "候选人ID不能为空")
    private Long candidateId;

    @NotNull(message = "面试轮次不能为空")
    private InterviewRound interviewRound;

    private Long interviewerId;

    private String interviewerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;

    private BigDecimal score;

    private String evaluation;

    private Boolean isPassed;

    private String remark;

    private Long createdBy;

    private String createdByName;
}
