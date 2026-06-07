package com.example.employee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.CalendarSubscription;
import com.example.employee.mapper.CalendarSubscriptionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarSubscriptionService extends ServiceImpl<CalendarSubscriptionMapper, CalendarSubscription> {

    public List<CalendarSubscription> findBySubscriberId(Long subscriberId) {
        LambdaQueryWrapper<CalendarSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalendarSubscription::getSubscriberId, subscriberId);
        return list(wrapper);
    }

    public List<Long> findSubscribedEmployeeIds(Long subscriberId) {
        return findBySubscriberId(subscriberId).stream()
                .map(CalendarSubscription::getTargetEmployeeId)
                .toList();
    }

    public boolean exists(Long subscriberId, Long targetEmployeeId) {
        LambdaQueryWrapper<CalendarSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalendarSubscription::getSubscriberId, subscriberId)
                .eq(CalendarSubscription::getTargetEmployeeId, targetEmployeeId);
        return count(wrapper) > 0;
    }
}
