package com.example.employee.vo;

import lombok.Data;

import java.util.List;

@Data
public class MessagePreviewVO {
    private Long unreadCount;
    private List<MessageVO> latestMessages;
}
