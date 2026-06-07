package com.example.employee.service.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.system.SysConfigHistory;
import com.example.employee.mapper.system.SysConfigHistoryMapper;
import com.example.employee.vo.ConfigHistoryVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SysConfigHistoryService extends ServiceImpl<SysConfigHistoryMapper, SysConfigHistory> {

    public void recordChange(String configGroup, String configKey, String displayName,
                             String oldValue, String newValue, String changedBy) {
        SysConfigHistory history = new SysConfigHistory();
        history.setConfigGroup(configGroup);
        history.setConfigKey(configKey);
        history.setDisplayName(displayName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedBy(changedBy != null ? changedBy : "system");
        history.setChangedAt(LocalDateTime.now());
        this.save(history);
    }

    public IPage<ConfigHistoryVO> queryHistory(String configGroup, String configKey, int pageNum, int pageSize) {
        LambdaQueryWrapper<SysConfigHistory> wrapper = new LambdaQueryWrapper<>();
        if (configGroup != null && !configGroup.isEmpty()) {
            wrapper.eq(SysConfigHistory::getConfigGroup, configGroup);
        }
        if (configKey != null && !configKey.isEmpty()) {
            wrapper.eq(SysConfigHistory::getConfigKey, configKey);
        }
        wrapper.orderByDesc(SysConfigHistory::getChangedAt);

        Page<SysConfigHistory> page = new Page<>(pageNum, pageSize);
        IPage<SysConfigHistory> historyPage = this.page(page, wrapper);

        Page<ConfigHistoryVO> resultPage = new Page<>(pageNum, pageSize, historyPage.getTotal());
        List<ConfigHistoryVO> voList = historyPage.getRecords().stream()
                .map(this::toVO)
                .toList();
        resultPage.setRecords(voList);
        return resultPage;
    }

    private ConfigHistoryVO toVO(SysConfigHistory history) {
        ConfigHistoryVO vo = new ConfigHistoryVO();
        vo.setId(history.getId());
        vo.setConfigGroup(history.getConfigGroup());
        vo.setConfigKey(history.getConfigKey());
        vo.setDisplayName(history.getDisplayName());
        vo.setOldValue(history.getOldValue());
        vo.setNewValue(history.getNewValue());
        vo.setChangedBy(history.getChangedBy());
        vo.setChangedAt(history.getChangedAt() != null ?
                history.getChangedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        return vo;
    }
}
