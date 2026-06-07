package com.example.employee.entity.schedule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("schedule_alert")
public class ScheduleAlert {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("alert_type")
    private String alertType;

    private String severity;

    private String department;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("alert_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alertDate;

    @TableField("alert_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alertStartDate;

    @TableField("alert_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alertEndDate;

    private String message;

    private String detail;

    @TableField("is_resolved")
    private Boolean isResolved;

    @TableField("resolved_by")
    private Long resolvedBy;

    @TableField("resolved_by_name")
    private String resolvedByName;

    @TableField("resolved_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedAt;

    @TableField("resolve_note")
    private String resolveNote;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
