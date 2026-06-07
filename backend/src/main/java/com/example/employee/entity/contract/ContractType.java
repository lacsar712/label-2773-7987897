package com.example.employee.entity.contract;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ContractType {
    FIXED_TERM("FIXED_TERM", "固定期限"),
    OPEN_ENDED("OPEN_ENDED", "无固定期限"),
    INTERNSHIP("INTERNSHIP", "实习"),
    LABOR_SERVICE("LABOR_SERVICE", "劳务");

    @EnumValue
    private final String code;
    private final String displayName;

    ContractType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
