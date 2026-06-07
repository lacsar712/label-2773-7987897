package com.example.employee.entity.system;

import lombok.Getter;

@Getter
public enum ConfigGroup {
    COMPANY_INFO("公司信息", "公司基础信息配置，包括名称、Logo、域名等"),
    BUSINESS_RULES("业务规则", "系统业务规则配置，包括分页、考勤、年假、合同预警等"),
    SECURITY_POLICY("安全策略", "系统安全策略配置，包括会话超时、密码复杂度、登录锁定等"),
    FEATURE_TOGGLE("功能开关", "系统功能开关配置，包括自助注册、邮件通知、附件上传等");

    private final String displayName;
    private final String description;

    ConfigGroup(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
