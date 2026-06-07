package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum EvaluationCycle {
    QUARTERLY("QUARTERLY"),
    SEMI_ANNUAL("SEMI_ANNUAL"),
    ANNUAL("ANNUAL");

    @EnumValue
    private final String code;

    EvaluationCycle(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
