package com.example.employee.service.attachment;

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
