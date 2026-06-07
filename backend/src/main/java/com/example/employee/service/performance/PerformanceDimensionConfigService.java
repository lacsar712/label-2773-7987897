package com.example.employee.service.performance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.entity.performance.PerformanceDimensionConfig;
import com.example.employee.mapper.performance.PerformanceDimensionConfigMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceDimensionConfigService extends ServiceImpl<PerformanceDimensionConfigMapper, PerformanceDimensionConfig> {

    public List<PerformanceDimensionConfig> getActiveDimensions() {
        LambdaQueryWrapper<PerformanceDimensionConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceDimensionConfig::getIsActive, true);
        wrapper.orderByAsc(PerformanceDimensionConfig::getSortOrder);
        return this.list(wrapper);
    }

    public List<PerformanceDimensionConfig> getAllDimensions() {
        LambdaQueryWrapper<PerformanceDimensionConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PerformanceDimensionConfig::getSortOrder);
        return this.list(wrapper);
    }
}
