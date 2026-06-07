package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractTimelineVO {
    private Long contractId;

    private String contractNo;

    private String eventType;

    private String eventName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
