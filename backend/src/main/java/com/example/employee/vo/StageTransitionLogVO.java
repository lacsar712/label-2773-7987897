package com.example.employee.vo;

import com.example.employee.entity.recruitment.CandidateStage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StageTransitionLogVO {
    private Long id;

    private Long candidateId;

    private String candidateName;

    private CandidateStage fromStage;

    private String fromStageName;

    private CandidateStage toStage;

    private String toStageName;

    private Long operatorId;

    private String operatorName;

    private String transitionReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transitionTime;

    private String remark;
}
