package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum EvaluationStage {
    SELF_EVALUATION("SELF_EVALUATION"),
    MANAGER_REVIEW("MANAGER_REVIEW"),
    HR_REVIEW("HR_REVIEW"),
    CONFIRMED("CONFIRMED"),
    ARCHIVED("ARCHIVED");

    @EnumValue
    private final String code;

    EvaluationStage(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
