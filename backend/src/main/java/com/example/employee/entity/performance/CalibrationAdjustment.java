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
@TableName("calibration_adjustment")
public class CalibrationAdjustment {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("meeting_id")
    private Long meetingId;

    @TableField("evaluation_id")
    private Long evaluationId;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("original_grade")
    private PerformanceGrade originalGrade;

    @TableField("original_score")
    private BigDecimal originalScore;

    @TableField("original_rank")
    private Integer originalRank;

    @TableField("adjusted_grade")
    private PerformanceGrade adjustedGrade;

    @TableField("adjusted_score")
    private BigDecimal adjustedScore;

    @TableField("adjusted_rank")
    private Integer adjustedRank;

    @TableField("adjustment_reason")
    private String adjustmentReason;

    @TableField("adjusted_by")
    private Long adjustedBy;

    @TableField("adjusted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime adjustedAt;
}
