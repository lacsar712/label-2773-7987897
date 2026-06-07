package com.example.employee.entity.system;

import lombok.Getter;

@Getter
public enum ConfigValueType {
    STRING("字符串"),
    INTEGER("整数"),
    BOOLEAN("布尔值"),
    NUMBER("数值");

    private final String displayName;

    ConfigValueType(String displayName) {
        this.displayName = displayName;
    }
}
