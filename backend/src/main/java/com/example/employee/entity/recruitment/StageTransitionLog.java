package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stage_transition_log")
public class StageTransitionLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("candidate_id")
    private Long candidateId;

    @TableField("candidate_name")
    private String candidateName;

    @TableField("from_stage")
    private CandidateStage fromStage;

    @TableField("to_stage")
    private CandidateStage toStage;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("transition_reason")
    private String transitionReason;

    @TableField("transition_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transitionTime;

    @TableField("remark")
    private String remark;
}
