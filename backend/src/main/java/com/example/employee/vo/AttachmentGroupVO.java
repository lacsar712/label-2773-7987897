package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AttachmentGroupVO {
    private String attachmentGroupId;

    private Long categoryId;

    private String categoryCode;

    private String categoryName;

    private List<AttachmentVersionVO> versions;
}

@Data
class AttachmentVersionVO {
    private Long id;

    private Long employeeId;

    private String employeeName;

    private String fileName;

    private Long fileSize;

    private String mimeType;

    private String fileExtension;

    private Integer version;

    private Boolean isLatest;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private Boolean isExpired;

    private Long uploaderId;

    private String uploaderName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;

    private String description;
}
