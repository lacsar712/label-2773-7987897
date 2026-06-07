package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum PerformanceGrade {
    S("S"),
    A("A"),
    B("B"),
    C("C"),
    D("D");

    @EnumValue
    private final String code;

    PerformanceGrade(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
