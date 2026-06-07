package com.example.employee.dto;

import lombok.Data;

@Data
public class SystemConfigDTO {
    private String configGroup;
    private String configKey;
    private String configValue;
    private String valueType;
    private String displayName;
    private String description;
    private Integer sortOrder;
    private String updatedBy;
}
