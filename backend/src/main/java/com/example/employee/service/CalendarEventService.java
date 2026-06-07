package com.example.employee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.CalendarEvent;
import com.example.employee.entity.EventType;
import com.example.employee.mapper.CalendarEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarEventService extends ServiceImpl<CalendarEventMapper, CalendarEvent> {

    public List<CalendarEvent> findByTimeRangeAndFilters(
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<String> eventTypes,
            List<Long> employeeIds) {
        return baseMapper.findByTimeRangeAndFilters(startTime, endTime, eventTypes, employeeIds);
    }

    public List<CalendarEvent> findByDate(LocalDateTime dateStart, LocalDateTime dateEnd) {
        LambdaQueryWrapper<CalendarEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(CalendarEvent::getStartTime, dateEnd)
                .ge(CalendarEvent::getEndTime, dateStart)
                .eq(CalendarEvent::getIsPublic, true)
                .orderByAsc(CalendarEvent::getStartTime);
        return list(wrapper);
    }

    @Override
    public boolean save(CalendarEvent entity) {
        if (!StringUtils.hasText(entity.getColor())) {
            try {
                EventType eventType = EventType.valueOf(entity.getEventType());
                entity.setColor(eventType.getDefaultColor());
            } catch (IllegalArgumentException e) {
                entity.setColor(EventType.CUSTOM.getDefaultColor());
            }
        }
        if (entity.getIsAllDay() == null) {
            entity.setIsAllDay(false);
        }
        if (entity.getIsPublic() == null) {
            entity.setIsPublic(true);
        }
        if (!StringUtils.hasText(entity.getSourceModule())) {
            entity.setSourceModule("CUSTOM");
        }
        return super.save(entity);
    }

    public boolean syncFromSource(CalendarEvent event) {
        LambdaQueryWrapper<CalendarEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalendarEvent::getSourceModule, event.getSourceModule())
                .eq(CalendarEvent::getSourceId, event.getSourceId());
        CalendarEvent existing = getOne(wrapper);
        if (existing != null) {
            event.setId(existing.getId());
            return updateById(event);
        } else {
            return save(event);
        }
    }

    public boolean deleteBySource(String sourceModule, String sourceId) {
        LambdaQueryWrapper<CalendarEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalendarEvent::getSourceModule, sourceModule)
                .eq(CalendarEvent::getSourceId, sourceId);
        return remove(wrapper);
    }
}
