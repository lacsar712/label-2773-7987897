package com.example.employee.entity.skill;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SkillCategory {
    LANGUAGE("LANGUAGE", "编程语言"),
    FRAMEWORK("FRAMEWORK", "框架工具"),
    SOFT_SKILL("SOFT_SKILL", "软技能"),
    CERTIFICATE("CERTIFICATE", "专业证书"),
    DATABASE("DATABASE", "数据库"),
    DEVOPS("DEVOPS", "运维部署"),
    OTHER("OTHER", "其他");

    @EnumValue
    private final String code;
    private final String description;

    SkillCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
