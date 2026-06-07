package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("performance_evaluation")
public class PerformanceEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("batch_id")
    private Long batchId;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("department")
    private String department;

    @TableField("manager_id")
    private Long managerId;

    @TableField("manager_name")
    private String managerName;

    @TableField("stage")
    private EvaluationStage stage;

    @TableField("self_score")
    private BigDecimal selfScore;

    @TableField("self_comment")
    private String selfComment;

    @TableField("self_submitted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime selfSubmittedAt;

    @TableField("manager_score")
    private BigDecimal managerScore;

    @TableField("final_grade")
    private PerformanceGrade finalGrade;

    @TableField("manager_comment")
    private String managerComment;

    @TableField("improvement_plan")
    private String improvementPlan;

    @TableField("manager_submitted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime managerSubmittedAt;

    @TableField("hr_comment")
    private String hrComment;

    @TableField("hr_reviewed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hrReviewedAt;

    @TableField("salary_adjustment_suggestion")
    private String salaryAdjustmentSuggestion;

    @TableField("confirmed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmedAt;

    @TableField("is_locked")
    private Boolean isLocked;

    @TableField("rank_in_dept")
    private Integer rankInDept;

    @TableField("potential_rating")
    private String potentialRating;

    @TableField("performance_rating")
    private String performanceRating;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
