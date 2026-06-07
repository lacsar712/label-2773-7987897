package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("performance_appeal")
public class PerformanceAppeal {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("evaluation_id")
    private Long evaluationId;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("appeal_reason")
    private String appealReason;

    @TableField("appeal_detail")
    private String appealDetail;

    @TableField("status")
    private AppealStatus status;

    @TableField("reviewer_id")
    private Long reviewerId;

    @TableField("reviewer_name")
    private String reviewerName;

    @TableField("review_comment")
    private String reviewComment;

    @TableField("reviewed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
