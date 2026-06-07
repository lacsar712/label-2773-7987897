package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OfferApprovalStatus {
    PENDING("PENDING", "待审批"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝");

    @EnumValue
    private final String code;
    private final String displayName;

    OfferApprovalStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OfferApprovalStatus fromCode(String code) {
        for (OfferApprovalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
