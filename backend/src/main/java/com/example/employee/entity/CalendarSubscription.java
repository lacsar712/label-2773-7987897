package com.example.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("calendar_subscription")
public class CalendarSubscription {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "订阅人ID不能为空")
    @TableField("subscriber_id")
    private Long subscriberId;

    @TableField("subscriber_name")
    private String subscriberName;

    @NotNull(message = "目标员工ID不能为空")
    @TableField("target_employee_id")
    private Long targetEmployeeId;

    @TableField("target_employee_name")
    private String targetEmployeeName;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
