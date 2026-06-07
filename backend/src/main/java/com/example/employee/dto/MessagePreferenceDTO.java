package com.example.employee.dto;

import lombok.Data;

@Data
public class MessagePreferenceDTO {
    private Long employeeId;
    private String eventType;
    private Boolean pushEnabled;
}
