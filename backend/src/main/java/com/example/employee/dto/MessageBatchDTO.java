package com.example.employee.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageBatchDTO {
    private Long employeeId;
    private List<Long> messageIds;
}
