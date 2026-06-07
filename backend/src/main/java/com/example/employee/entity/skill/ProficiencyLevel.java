package com.example.employee.entity.skill;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ProficiencyLevel {
    LEVEL_1(1, "入门"),
    LEVEL_2(2, "初级"),
    LEVEL_3(3, "中级"),
    LEVEL_4(4, "高级"),
    LEVEL_5(5, "专家");

    @EnumValue
    private final Integer level;
    private final String description;

    ProficiencyLevel(Integer level, String description) {
        this.level = level;
        this.description = description;
    }

    public Integer getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public static ProficiencyLevel fromLevel(Integer level) {
        for (ProficiencyLevel pl : values()) {
            if (pl.getLevel().equals(level)) {
                return pl;
            }
        }
        throw new IllegalArgumentException("Invalid proficiency level: " + level);
    }
}
