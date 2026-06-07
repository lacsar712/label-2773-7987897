package com.example.employee.entity.recruitment;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum CandidateSource {
    INTERNAL_REFERRAL("INTERNAL_REFERRAL", "内推"),
    RECRUITMENT_WEBSITE("RECRUITMENT_WEBSITE", "招聘网站"),
    SOCIAL_MEDIA("SOCIAL_MEDIA", "社交媒体"),
    CAMPUS_RECRUITMENT("CAMPUS_RECRUITMENT", "校园招聘"),
    HEADHUNTER("HEADHUNTER", "猎头"),
    DIRECT_APPLICATION("DIRECT_APPLICATION", "主动投递"),
    OTHER("OTHER", "其他");

    @EnumValue
    private final String code;
    private final String displayName;

    CandidateSource(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CandidateSource fromCode(String code) {
        for (CandidateSource source : values()) {
            if (source.code.equals(code)) {
                return source;
            }
        }
        return null;
    }
}
