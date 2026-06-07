package com.example.employee.vo;

import lombok.Data;

@Data
public class ConfigHistoryVO {
    private Long id;
    private String configGroup;
    private String configKey;
    private String displayName;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private String changedAt;
}
