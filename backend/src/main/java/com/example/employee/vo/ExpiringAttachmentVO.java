package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpiringAttachmentVO {
    private Long attachmentId;

    private Long employeeId;

    private String employeeName;

    private String department;

    private String categoryName;

    private String fileName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private Long daysUntilExpiry;

    private String attachmentGroupId;
}
