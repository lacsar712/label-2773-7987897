package com.example.employee.entity.contract;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ContractStatus {
    DRAFT("DRAFT", "草稿"),
    ACTIVE("ACTIVE", "生效"),
    EXPIRED("EXPIRED", "到期"),
    TERMINATED("TERMINATED", "终止"),
    RENEWING("RENEWING", "续签中");

    @EnumValue
    private final String code;
    private final String displayName;

    ContractStatus(String code, String displayName) {
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
