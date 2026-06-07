package com.example.employee.entity.message;

public enum MessageEventType {
    APPROVAL_FLOW("审批流转", "待您审批的流程有新动态"),
    ANNOUNCEMENT("公告发布", "公司发布了新的公告"),
    CONTRACT_EXPIRY("合同到期", "您的劳动合同即将到期"),
    ONBOARDING_OVERDUE("入职清单逾期", "您的入职清单存在逾期未完成项"),
    ATTENDANCE_ABNORMAL("考勤异常", "您的考勤记录存在异常"),
    SALARY_PAID("薪资发放", "本月薪资已发放"),
    PERFORMANCE_REMIND("绩效提醒", "绩效考核相关提醒"),
    ATTACHMENT_EXPIRY("附件到期", "您的附件即将到期"),
    SYSTEM("系统通知", "系统维护或其他通知");

    private final String displayName;
    private final String defaultDescription;

    MessageEventType(String displayName, String defaultDescription) {
        this.displayName = displayName;
        this.defaultDescription = defaultDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }
}
