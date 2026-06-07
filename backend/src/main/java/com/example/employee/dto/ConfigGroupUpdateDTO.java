package com.example.employee.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConfigGroupUpdateDTO {
    private String configGroup;
    private List<SystemConfigDTO> configs;
    private String updatedBy;
}
