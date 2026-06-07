package com.example.employee.vo;

import lombok.Data;

@Data
public class MessagePreferenceVO {
    private String eventType;
    private String eventTypeName;
    private Boolean pushEnabled;
}
