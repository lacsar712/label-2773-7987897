package com.example.employee.service.attachment;

import com.example.employee.entity.message.MessageEventType;
import com.example.employee.service.message.SysMessageService;
import com.example.employee.vo.ExpiringAttachmentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttachmentExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentExpiryScheduler.class);

    @Autowired
    private EmployeeAttachmentService attachmentService;

    @Autowired
    private SysMessageService messageService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkAndMarkExpiredAttachments() {
        logger.info("开始检查过期附件...");
        attachmentService.markExpiredAttachments();
        logger.info("过期附件检查完成");
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public void sendExpiryReminders() {
        logger.info("开始发送到期提醒...");
        try {
            List<ExpiringAttachmentVO> expiringIn30Days = attachmentService.getExpiringAttachments(30);
            if (!expiringIn30Days.isEmpty()) {
                logger.info("发现 {} 个即将到期的附件需要提醒:", expiringIn30Days.size());
                for (ExpiringAttachmentVO vo : expiringIn30Days) {
                    logger.info("员工 [{}] 的 [{}] 文件 [{}] 将在 {} 天后到期",
                            vo.getEmployeeName(),
                            vo.getCategoryName(),
                            vo.getFileName(),
                            vo.getDaysUntilExpiry());
                    String title = "附件即将到期提醒";
                    String summary = String.format("您的[%s]文件[%s]将在%d天后到期，请及时续签或更新",
                            vo.getCategoryName(), vo.getFileName(), vo.getDaysUntilExpiry());
                    messageService.sendMessage(
                            vo.getEmployeeId(),
                            MessageEventType.ATTACHMENT_EXPIRY,
                            title,
                            summary,
                            "ATTACHMENT",
                            String.valueOf(vo.getAttachmentId()),
                            "/attachments"
                    );
                }
            } else {
                logger.info("当前没有即将到期的附件");
            }
        } catch (Exception e) {
            logger.error("发送到期提醒失败", e);
        }
        logger.info("到期提醒处理完成");
    }
}
