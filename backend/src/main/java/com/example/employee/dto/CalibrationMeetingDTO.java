package com.example.employee.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CalibrationMeetingDTO {
    private Long batchId;
    private String department;
    private String meetingName;
    private LocalDateTime meetingDate;
    private String participants;
    private String meetingNotes;
    private List<CalibrationAdjustmentDTO> adjustments;
}
