package com.example.employee.vo;

import lombok.Data;

@Data
public class ConfigItemVO {
    private Long id;
    private String configGroup;
    private String configKey;
    private String configValue;
    private String valueType;
    private String displayName;
    private String description;
    private Integer sortOrder;
    private String updatedBy;
    private String updatedAt;
}
