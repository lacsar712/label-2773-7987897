package com.example.employee.entity;

import lombok.Getter;

@Getter
public enum EventType {
    LEAVE("请假", "#FF7875"),
    ONBOARDING("入职", "#36CFC9"),
    ANNIVERSARY("司龄", "#FFC53D"),
    DEPT_ACTIVITY("部门活动", "#69C0FF"),
    HOLIDAY("节假日", "#95DE64"),
    INTERVIEW("面试", "#B37FEB"),
    CUSTOM("自定义", "#F759AB");

    private final String displayName;
    private final String defaultColor;

    EventType(String displayName, String defaultColor) {
        this.displayName = displayName;
        this.defaultColor = defaultColor;
    }
}
