package com.example.employee.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageArchiveScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MessageArchiveScheduler.class);

    @Autowired
    private SysMessageService messageService;

    @Scheduled(cron = "0 10 2 * * ?")
    public void archiveOldMessages() {
        logger.info("开始自动归档30天前的已读消息...");
        try {
            int count = messageService.archiveOldReadMessages();
            logger.info("自动归档完成，共归档 {} 条消息", count);
        } catch (Exception e) {
            logger.error("自动归档消息失败", e);
        }
    }
}
