package com.example.employee.vo;

import lombok.Data;

import java.util.List;

@Data
public class ConfigGroupVO {
    private String configGroup;
    private String displayName;
    private String description;
    private List<ConfigItemVO> configs;
}
