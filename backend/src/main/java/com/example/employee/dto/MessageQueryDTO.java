package com.example.employee.dto;

import lombok.Data;

@Data
public class MessageQueryDTO {
    private Long employeeId;
    private String status;
    private String eventType;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
