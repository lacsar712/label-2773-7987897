package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.schedule.ScheduleChangeLog;
import com.example.employee.mapper.schedule.ScheduleChangeLogMapper;
import com.example.employee.vo.ScheduleChangeLogVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleChangeLogService extends ServiceImpl<ScheduleChangeLogMapper, ScheduleChangeLog> {

    public List<ScheduleChangeLogVO> getChangeLogs(Long employeeId, LocalDate startDate, LocalDate endDate, Long operatorId) {
        LambdaQueryWrapper<ScheduleChangeLog> wrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            wrapper.eq(ScheduleChangeLog::getEmployeeId, employeeId);
        }
        if (startDate != null) {
            wrapper.ge(ScheduleChangeLog::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ScheduleChangeLog::getScheduleDate, endDate);
        }
        if (operatorId != null) {
            wrapper.eq(ScheduleChangeLog::getOperatorId, operatorId);
        }
        wrapper.orderByDesc(ScheduleChangeLog::getChangedAt);
        List<ScheduleChangeLog> logs = this.list(wrapper);
        return convertToVOList(logs);
    }

    private List<ScheduleChangeLogVO> convertToVOList(List<ScheduleChangeLog> logs) {
        if (logs == null) {
            return new ArrayList<>();
        }
        return logs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private ScheduleChangeLogVO convertToVO(ScheduleChangeLog log) {
        ScheduleChangeLogVO vo = new ScheduleChangeLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }
}
