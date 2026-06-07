package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("performance_batch")
public class PerformanceBatch {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String batchName;

    @TableField("cycle_type")
    private EvaluationCycle cycleType;

    @TableField("cycle_year")
    private Integer cycleYear;

    @TableField("cycle_quarter")
    private Integer cycleQuarter;

    @TableField("department")
    private String department;

    @TableField("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @TableField("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @TableField("self_eval_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime selfEvalDeadline;

    @TableField("manager_review_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime managerReviewDeadline;

    @TableField("hr_review_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hrReviewDeadline;

    @TableField("status")
    private EvaluationStage status;

    @TableField("description")
    private String description;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
