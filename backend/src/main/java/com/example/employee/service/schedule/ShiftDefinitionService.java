package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.ShiftDefinitionDTO;
import com.example.employee.entity.schedule.ShiftDefinition;
import com.example.employee.mapper.schedule.ShiftDefinitionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftDefinitionService extends ServiceImpl<ShiftDefinitionMapper, ShiftDefinition> {

    public List<ShiftDefinition> getShiftsByDepartment(String department) {
        LambdaQueryWrapper<ShiftDefinition> wrapper = new LambdaQueryWrapper<>();
        if (department != null && !department.isEmpty()) {
            wrapper.eq(ShiftDefinition::getDepartment, department);
        }
        wrapper.eq(ShiftDefinition::getIsActive, true)
                .orderByAsc(ShiftDefinition::getSortOrder);
        return this.list(wrapper);
    }

    @Transactional
    public ShiftDefinition createShift(ShiftDefinitionDTO dto, Long operatorId, String operatorName) {
        ShiftDefinition shift = new ShiftDefinition();
        BeanUtils.copyProperties(dto, shift);
        shift.setIsActive(true);
        shift.setCreatedBy(operatorId);
        shift.setCreatedByName(operatorName);
        shift.setCreatedAt(LocalDateTime.now());
        shift.setUpdatedAt(LocalDateTime.now());
        if (shift.getColor() == null || shift.getColor().isEmpty()) {
            shift.setColor("#1890FF");
        }
        if (shift.getSortOrder() == null) {
            shift.setSortOrder(0);
        }
        if (shift.getIsCrossDay() == null) {
            shift.setIsCrossDay(false);
        }
        this.save(shift);
        return shift;
    }

    @Transactional
    public ShiftDefinition updateShift(Long id, ShiftDefinitionDTO dto) {
        ShiftDefinition shift = this.getById(id);
        if (shift == null) {
            return null;
        }
        if (dto.getShiftCode() != null) shift.setShiftCode(dto.getShiftCode());
        if (dto.getShiftName() != null) shift.setShiftName(dto.getShiftName());
        if (dto.getStartTime() != null) shift.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) shift.setEndTime(dto.getEndTime());
        if (dto.getIsCrossDay() != null) shift.setIsCrossDay(dto.getIsCrossDay());
        if (dto.getColor() != null) shift.setColor(dto.getColor());
        if (dto.getSortOrder() != null) shift.setSortOrder(dto.getSortOrder());
        if (dto.getDescription() != null) shift.setDescription(dto.getDescription());
        shift.setUpdatedAt(LocalDateTime.now());
        this.updateById(shift);
        return shift;
    }

    @Transactional
    public boolean deactivateShift(Long id) {
        ShiftDefinition shift = this.getById(id);
        if (shift == null) {
            return false;
        }
        shift.setIsActive(false);
        shift.setUpdatedAt(LocalDateTime.now());
        return this.updateById(shift);
    }

    public ShiftDefinition getRestShift(String department) {
        LambdaQueryWrapper<ShiftDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShiftDefinition::getDepartment, department)
                .eq(ShiftDefinition::getShiftCode, "REST")
                .eq(ShiftDefinition::getIsActive, true);
        return this.getOne(wrapper);
    }
}
