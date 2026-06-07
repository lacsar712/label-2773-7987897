package com.example.employee.entity.contract;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SignStatus {
    PENDING("PENDING", "待签"),
    SIGNED("SIGNED", "已签"),
    REJECTED("REJECTED", "拒签");

    @EnumValue
    private final String code;
    private final String displayName;

    SignStatus(String code, String displayName) {
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
