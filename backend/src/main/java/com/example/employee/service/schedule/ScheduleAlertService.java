package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.schedule.ScheduleAlert;
import com.example.employee.mapper.schedule.ScheduleAlertMapper;
import com.example.employee.vo.ScheduleAlertVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleAlertService extends ServiceImpl<ScheduleAlertMapper, ScheduleAlert> {

    public List<ScheduleAlertVO> getAlerts(String department, LocalDate startDate, LocalDate endDate, Boolean isResolved) {
        LambdaQueryWrapper<ScheduleAlert> wrapper = new LambdaQueryWrapper<>();
        if (department != null && !department.isEmpty()) {
            wrapper.eq(ScheduleAlert::getDepartment, department);
        }
        if (startDate != null) {
            wrapper.ge(ScheduleAlert::getAlertDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ScheduleAlert::getAlertDate, endDate);
        }
        if (isResolved != null) {
            wrapper.eq(ScheduleAlert::getIsResolved, isResolved);
        }
        wrapper.orderByDesc(ScheduleAlert::getCreatedAt);
        return convertToVOList(this.list(wrapper));
    }

    @Transactional
    public ScheduleAlert createAlert(String alertType, String severity, String department,
                                     Long employeeId, String employeeName, LocalDate alertDate,
                                     LocalDate alertStartDate, LocalDate alertEndDate,
                                     String message, String detail) {
        ScheduleAlert alert = new ScheduleAlert();
        alert.setAlertType(alertType);
        alert.setSeverity(severity);
        alert.setDepartment(department);
        alert.setEmployeeId(employeeId);
        alert.setEmployeeName(employeeName);
        alert.setAlertDate(alertDate);
        alert.setAlertStartDate(alertStartDate);
        alert.setAlertEndDate(alertEndDate);
        alert.setMessage(message);
        alert.setDetail(detail);
        alert.setIsResolved(false);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setUpdatedAt(LocalDateTime.now());
        this.save(alert);
        return alert;
    }

    @Transactional
    public boolean resolveAlert(Long id, Long operatorId, String operatorName, String note) {
        ScheduleAlert alert = this.getById(id);
        if (alert == null) {
            return false;
        }
        alert.setIsResolved(true);
        alert.setResolvedBy(operatorId);
        alert.setResolvedByName(operatorName);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolveNote(note);
        alert.setUpdatedAt(LocalDateTime.now());
        return this.updateById(alert);
    }

    @Transactional
    public void clearResolvedAlertsForPeriod(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ScheduleAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleAlert::getIsResolved, false);
        if (startDate != null) {
            wrapper.ge(ScheduleAlert::getAlertDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ScheduleAlert::getAlertDate, endDate);
        }
        List<ScheduleAlert> alerts = this.list(wrapper);
        for (ScheduleAlert alert : alerts) {
            alert.setIsResolved(true);
            alert.setResolvedAt(LocalDateTime.now());
            alert.setResolveNote("自动清理");
        }
        this.updateBatchById(alerts);
    }

    private List<ScheduleAlertVO> convertToVOList(List<ScheduleAlert> alerts) {
        if (alerts == null) {
            return new ArrayList<>();
        }
        return alerts.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private ScheduleAlertVO convertToVO(ScheduleAlert alert) {
        ScheduleAlertVO vo = new ScheduleAlertVO();
        BeanUtils.copyProperties(alert, vo);
        return vo;
    }
}
