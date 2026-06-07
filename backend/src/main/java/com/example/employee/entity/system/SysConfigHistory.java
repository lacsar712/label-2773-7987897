package com.example.employee.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_config_history")
public class SysConfigHistory {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("config_group")
    private String configGroup;

    @TableField("config_key")
    private String configKey;

    @TableField("display_name")
    private String displayName;

    @TableField("old_value")
    private String oldValue;

    @TableField("new_value")
    private String newValue;

    @TableField("changed_by")
    private String changedBy;

    @TableField("changed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changedAt;
}
