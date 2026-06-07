package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;
    private String eventType;
    private String eventTypeName;
    private String title;
    private String summary;
    private String bizType;
    private String bizId;
    private String deepLink;
    private Boolean isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
