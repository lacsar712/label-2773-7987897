package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum InterviewRound {
    WRITTEN_TEST("WRITTEN_TEST", "笔试"),
    FIRST_INTERVIEW("FIRST_INTERVIEW", "一面"),
    SECOND_INTERVIEW("SECOND_INTERVIEW", "二面"),
    HR_INTERVIEW("HR_INTERVIEW", "HR面");

    @EnumValue
    private final String code;
    private final String displayName;

    InterviewRound(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InterviewRound fromStage(CandidateStage stage) {
        return switch (stage) {
            case WRITTEN_TEST -> WRITTEN_TEST;
            case FIRST_INTERVIEW -> FIRST_INTERVIEW;
            case SECOND_INTERVIEW -> SECOND_INTERVIEW;
            case HR_INTERVIEW -> HR_INTERVIEW;
            default -> null;
        };
    }

    public static InterviewRound fromCode(String code) {
        for (InterviewRound round : values()) {
            if (round.code.equals(code)) {
                return round;
            }
        }
        return null;
    }
}
