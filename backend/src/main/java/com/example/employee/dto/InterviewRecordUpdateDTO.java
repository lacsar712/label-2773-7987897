package com.example.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InterviewRecordUpdateDTO {
    private Long interviewerId;

    private String interviewerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;

    private BigDecimal score;

    private String evaluation;

    private Boolean isPassed;

    private String remark;
}
