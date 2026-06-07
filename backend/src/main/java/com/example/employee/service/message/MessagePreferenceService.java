package com.example.employee.service.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.MessagePreferenceDTO;
import com.example.employee.entity.message.MessageEventType;
import com.example.employee.entity.message.MessagePreference;
import com.example.employee.mapper.message.MessagePreferenceMapper;
import com.example.employee.vo.MessagePreferenceVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessagePreferenceService extends ServiceImpl<MessagePreferenceMapper, MessagePreference> {

    public boolean isPushEnabled(Long employeeId, MessageEventType eventType) {
        MessagePreference pref = this.getOne(new LambdaQueryWrapper<MessagePreference>()
                .eq(MessagePreference::getEmployeeId, employeeId)
                .eq(MessagePreference::getEventType, eventType.name()));
        if (pref == null) {
            return true;
        }
        return pref.getPushEnabled();
    }

    public List<MessagePreferenceVO> getPreferences(Long employeeId) {
        List<MessagePreferenceVO> result = new ArrayList<>();
        for (MessageEventType eventType : MessageEventType.values()) {
            MessagePreferenceVO vo = new MessagePreferenceVO();
            vo.setEventType(eventType.name());
            vo.setEventTypeName(eventType.getDisplayName());
            MessagePreference pref = this.getOne(new LambdaQueryWrapper<MessagePreference>()
                    .eq(MessagePreference::getEmployeeId, employeeId)
                    .eq(MessagePreference::getEventType, eventType.name()));
            vo.setPushEnabled(pref == null || pref.getPushEnabled());
            result.add(vo);
        }
        return result;
    }

    @Transactional
    public boolean updatePreference(MessagePreferenceDTO dto) {
        MessagePreference pref = this.getOne(new LambdaQueryWrapper<MessagePreference>()
                .eq(MessagePreference::getEmployeeId, dto.getEmployeeId())
                .eq(MessagePreference::getEventType, dto.getEventType()));
        if (pref == null) {
            pref = new MessagePreference();
            pref.setEmployeeId(dto.getEmployeeId());
            pref.setEventType(dto.getEventType());
            pref.setPushEnabled(dto.getPushEnabled());
            pref.setUpdatedAt(LocalDateTime.now());
            return this.save(pref);
        }
        pref.setPushEnabled(dto.getPushEnabled());
        pref.setUpdatedAt(LocalDateTime.now());
        return this.updateById(pref);
    }
}
