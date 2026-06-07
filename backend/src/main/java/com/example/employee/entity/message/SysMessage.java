package com.example.employee.entity.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class SysMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("event_type")
    private String eventType;

    private String title;

    private String summary;

    @TableField("biz_type")
    private String bizType;

    @TableField("biz_id")
    private String bizId;

    @TableField("deep_link")
    private String deepLink;

    @TableField("is_read")
    private Boolean isRead;

    @TableField("read_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;

    @TableField("is_archived")
    private Boolean isArchived;

    @TableField("archived_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime archivedAt;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
