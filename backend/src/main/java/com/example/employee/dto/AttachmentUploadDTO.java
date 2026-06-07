package com.example.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class AttachmentUploadDTO {
    private Long employeeId;

    private Long categoryId;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private Long uploaderId;

    private String uploaderName;

    private MultipartFile file;
}
