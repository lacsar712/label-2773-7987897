package com.example.employee.service.contract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContractExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ContractExpiryScheduler.class);

    @Autowired
    private EmployeeContractService contractService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAndMarkExpiredContracts() {
        logger.info("开始检查过期合同...");
        try {
            contractService.checkAndMarkExpiredContracts();
            logger.info("过期合同检查完成");
        } catch (Exception e) {
            logger.error("检查过期合同失败", e);
        }
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public void sendExpiryWarnings() {
        logger.info("开始发送合同到期预警...");
        try {
            int sentCount = contractService.sendExpiryWarnings();
            logger.info("合同到期预警处理完成，共发送 {} 条提醒", sentCount);
        } catch (Exception e) {
            logger.error("发送合同到期预警失败", e);
        }
    }
}
