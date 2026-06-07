package com.example.employee.vo;

import com.example.employee.entity.recruitment.CandidateStage;
import lombok.Data;

import java.util.List;

@Data
public class CandidateKanbanColumnVO {
    private CandidateStage stage;

    private String stageName;

    private int order;

    private List<CandidateVO> candidates;

    private int totalCount;
}
