package com.example.employee.dto;

import com.example.employee.entity.recruitment.CandidateSource;
import com.example.employee.entity.recruitment.CandidateStage;
import lombok.Data;

@Data
public class CandidateQueryDTO {
    private String keyword;

    private CandidateStage stage;

    private CandidateSource sourceChannel;

    private String appliedPosition;

    private String department;

    private Boolean isInTalentPool;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
