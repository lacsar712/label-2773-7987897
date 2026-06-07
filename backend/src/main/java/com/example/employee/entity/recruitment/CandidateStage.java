package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;
import java.util.List;

public enum CandidateStage {
    RESUME_SCREENING("RESUME_SCREENING", "简历筛选", 1),
    WRITTEN_TEST("WRITTEN_TEST", "笔试", 2),
    FIRST_INTERVIEW("FIRST_INTERVIEW", "一面", 3),
    SECOND_INTERVIEW("SECOND_INTERVIEW", "二面", 4),
    HR_INTERVIEW("HR_INTERVIEW", "HR面", 5),
    OFFER_APPROVAL("OFFER_APPROVAL", "Offer审批", 6),
    HIRED("HIRED", "已入职", 7),
    ELIMINATED("ELIMINATED", "已淘汰", 8);

    @EnumValue
    private final String code;
    private final String displayName;
    private final int order;

    CandidateStage(String code, String displayName, int order) {
        this.code = code;
        this.displayName = displayName;
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    public static List<CandidateStage> getRequiredStages() {
        return Arrays.asList(
                RESUME_SCREENING,
                WRITTEN_TEST,
                FIRST_INTERVIEW,
                SECOND_INTERVIEW,
                HR_INTERVIEW
        );
    }

    public boolean canTransitionTo(CandidateStage target) {
        if (this == target) {
            return true;
        }
        if (target == ELIMINATED) {
            return this != HIRED;
        }
        if (this == ELIMINATED) {
            return target == RESUME_SCREENING;
        }
        if (this == HIRED) {
            return false;
        }
        if (target == HIRED) {
            return this == OFFER_APPROVAL;
        }
        return target.order == this.order + 1;
    }

    public static CandidateStage fromCode(String code) {
        for (CandidateStage stage : values()) {
            if (stage.code.equals(code)) {
                return stage;
            }
        }
        return null;
    }
}
