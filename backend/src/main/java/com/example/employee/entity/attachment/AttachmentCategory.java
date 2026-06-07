package com.example.employee.entity.attachment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attachment_category")
public class AttachmentCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("category_code")
    private String categoryCode;

    @TableField("category_name")
    private String categoryName;

    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("is_active")
    private Boolean isActive;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
