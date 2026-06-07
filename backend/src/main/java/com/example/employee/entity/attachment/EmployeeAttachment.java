package com.example.employee.entity.attachment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("employee_attachment")
public class EmployeeAttachment {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    private String department;

    @TableField("attachment_group_id")
    private String attachmentGroupId;

    @TableField("category_id")
    private Long categoryId;

    @TableField("category_code")
    private String categoryCode;

    @TableField("category_name")
    private String categoryName;

    @TableField("file_name")
    private String fileName;

    @TableField("stored_file_name")
    private String storedFileName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_size")
    private Long fileSize;

    @TableField("mime_type")
    private String mimeType;

    @TableField("file_extension")
    private String fileExtension;

    private Integer version;

    @TableField("is_latest")
    private Boolean isLatest;

    @TableField("expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @TableField("is_expired")
    private Boolean isExpired;

    @TableField("expiry_reminder_sent")
    private Boolean expiryReminderSent;

    @TableField("uploader_id")
    private Long uploaderId;

    @TableField("uploader_name")
    private String uploaderName;

    @TableField("uploaded_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;

    private String description;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("deleted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    @TableField("deleted_by")
    private Long deletedBy;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
