package com.example.employee.service.skill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.skill.SkillChangeLog;
import com.example.employee.mapper.skill.SkillChangeLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillChangeLogService extends ServiceImpl<SkillChangeLogMapper, SkillChangeLog> {

    public List<SkillChangeLog> listByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<SkillChangeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillChangeLog::getEmployeeId, employeeId);
        wrapper.orderByDesc(SkillChangeLog::getCreatedAt);
        return list(wrapper);
    }

    public List<SkillChangeLog> listBySkillTagId(Long skillTagId) {
        LambdaQueryWrapper<SkillChangeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillChangeLog::getSkillTagId, skillTagId);
        wrapper.orderByDesc(SkillChangeLog::getCreatedAt);
        return list(wrapper);
    }
}
