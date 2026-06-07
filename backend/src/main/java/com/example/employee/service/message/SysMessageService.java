package com.example.employee.service.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.MessageBatchDTO;
import com.example.employee.dto.MessageQueryDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.message.MessageEventType;
import com.example.employee.entity.message.SysMessage;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.message.SysMessageMapper;
import com.example.employee.vo.MessagePreviewVO;
import com.example.employee.vo.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMessageService extends ServiceImpl<SysMessageMapper, SysMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SysMessageService.class);

    @Autowired
    private MessagePreferenceService preferenceService;

    @Autowired
    private EmployeeMapper employeeMapper;

    public void sendMessage(Long employeeId, MessageEventType eventType, String title, String summary,
                            String bizType, String bizId, String deepLink) {
        if (!preferenceService.isPushEnabled(employeeId, eventType)) {
            logger.info("员工 {} 的消息类型 {} 已关闭推送，跳过发送", employeeId, eventType);
            return;
        }
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            logger.warn("员工 {} 不存在，跳过发送消息", employeeId);
            return;
        }
        SysMessage message = new SysMessage();
        message.setEmployeeId(employeeId);
        message.setEmployeeName(employee.getName());
        message.setEventType(eventType.name());
        message.setTitle(title);
        message.setSummary(summary);
        message.setBizType(bizType);
        message.setBizId(bizId);
        message.setDeepLink(deepLink);
        message.setIsRead(false);
        message.setIsArchived(false);
        message.setCreatedAt(LocalDateTime.now());
        this.save(message);
        logger.info("已发送消息给员工 {}: {}", employeeId, title);
    }

    public void sendMessageToAll(MessageEventType eventType, String title, String summary,
                                 String bizType, String bizId, String deepLink) {
        List<Employee> employees = employeeMapper.selectList(null);
        for (Employee emp : employees) {
            sendMessage(emp.getId(), eventType, title, summary, bizType, bizId, deepLink);
        }
    }

    public MessagePreviewVO getPreview(Long employeeId) {
        MessagePreviewVO vo = new MessagePreviewVO();
        Long unreadCount = this.count(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getEmployeeId, employeeId)
                .eq(SysMessage::getIsRead, false)
                .eq(SysMessage::getIsArchived, false));
        vo.setUnreadCount(unreadCount);

        List<SysMessage> latest = this.list(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getEmployeeId, employeeId)
                .eq(SysMessage::getIsArchived, false)
                .orderByDesc(SysMessage::getCreatedAt)
                .last("LIMIT 3"));
        vo.setLatestMessages(convertToVOList(latest));
        return vo;
    }

    public IPage<MessageVO> queryMessages(MessageQueryDTO dto) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getEmployeeId, dto.getEmployeeId())
                .eq(SysMessage::getIsArchived, false);

        if ("UNREAD".equals(dto.getStatus())) {
            wrapper.eq(SysMessage::getIsRead, false);
        } else if ("READ".equals(dto.getStatus())) {
            wrapper.eq(SysMessage::getIsRead, true);
        }

        if (dto.getEventType() != null && !dto.getEventType().isEmpty()) {
            wrapper.eq(SysMessage::getEventType, dto.getEventType());
        }

        wrapper.orderByDesc(SysMessage::getCreatedAt);

        Page<SysMessage> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<SysMessage> messagePage = this.page(page, wrapper);

        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        voPage.setRecords(convertToVOList(messagePage.getRecords()));
        return voPage;
    }

    @Transactional
    public boolean markAsRead(Long employeeId, Long messageId) {
        SysMessage message = this.getOne(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getId, messageId)
                .eq(SysMessage::getEmployeeId, employeeId));
        if (message == null) {
            return false;
        }
        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());
        return this.updateById(message);
    }

    @Transactional
    public int batchMarkAsRead(MessageBatchDTO dto) {
        if (dto.getMessageIds() == null || dto.getMessageIds().isEmpty()) {
            LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMessage::getEmployeeId, dto.getEmployeeId())
                    .eq(SysMessage::getIsRead, false)
                    .eq(SysMessage::getIsArchived, false);
            List<SysMessage> messages = this.list(wrapper);
            for (SysMessage msg : messages) {
                msg.setIsRead(true);
                msg.setReadAt(LocalDateTime.now());
            }
            this.updateBatchById(messages);
            return messages.size();
        }
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getEmployeeId, dto.getEmployeeId())
                .in(SysMessage::getId, dto.getMessageIds());
        List<SysMessage> messages = this.list(wrapper);
        for (SysMessage msg : messages) {
            if (!msg.getIsRead()) {
                msg.setIsRead(true);
                msg.setReadAt(LocalDateTime.now());
            }
        }
        this.updateBatchById(messages);
        return messages.size();
    }

    @Transactional
    public int batchClear(MessageBatchDTO dto) {
        if (dto.getMessageIds() == null || dto.getMessageIds().isEmpty()) {
            LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMessage::getEmployeeId, dto.getEmployeeId())
                    .eq(SysMessage::getIsArchived, false);
            List<SysMessage> messages = this.list(wrapper);
            for (SysMessage msg : messages) {
                msg.setIsArchived(true);
                msg.setArchivedAt(LocalDateTime.now());
            }
            this.updateBatchById(messages);
            return messages.size();
        }
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getEmployeeId, dto.getEmployeeId())
                .in(SysMessage::getId, dto.getMessageIds());
        List<SysMessage> messages = this.list(wrapper);
        for (SysMessage msg : messages) {
            msg.setIsArchived(true);
            msg.setArchivedAt(LocalDateTime.now());
        }
        this.updateBatchById(messages);
        return messages.size();
    }

    @Transactional
    public int archiveOldReadMessages() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getIsRead, true)
                .eq(SysMessage::getIsArchived, false)
                .lt(SysMessage::getReadAt, threshold);
        List<SysMessage> messages = this.list(wrapper);
        if (messages.isEmpty()) {
            return 0;
        }
        for (SysMessage msg : messages) {
            msg.setIsArchived(true);
            msg.setArchivedAt(LocalDateTime.now());
        }
        this.updateBatchById(messages);
        logger.info("已归档 {} 条30天前的已读消息", messages.size());
        return messages.size();
    }

    private List<MessageVO> convertToVOList(List<SysMessage> messages) {
        if (messages == null) {
            return new ArrayList<>();
        }
        return messages.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private MessageVO convertToVO(SysMessage msg) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(msg, vo);
        try {
            MessageEventType eventType = MessageEventType.valueOf(msg.getEventType());
            vo.setEventTypeName(eventType.getDisplayName());
        } catch (Exception e) {
            vo.setEventTypeName(msg.getEventType());
        }
        return vo;
    }
}
