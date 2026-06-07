package com.example.employee.dto;

import lombok.Data;

@Data
public class AttachmentQueryDTO {
    private Long employeeId;

    private Long categoryId;

    private String keyword;

    private Boolean onlyLatest;

    private Boolean includeExpired;
}
