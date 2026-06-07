package com.example.employee.service.audit;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.audit.AuditLog;
import com.example.employee.mapper.audit.AuditLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService extends ServiceImpl<AuditLogMapper, AuditLog> {

    public void log(String action, String operator, String targetType, String targetId, String detail) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setOperator(operator);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        this.save(log);
    }

    public void logImport(String operator, String taskId, String detail) {
        this.log("EMPLOYEE_IMPORT", operator, "IMPORT_TASK", taskId, detail);
    }
}
