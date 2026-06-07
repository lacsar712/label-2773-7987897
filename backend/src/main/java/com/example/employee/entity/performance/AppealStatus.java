package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum AppealStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED");

    @EnumValue
    private final String code;

    AppealStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
