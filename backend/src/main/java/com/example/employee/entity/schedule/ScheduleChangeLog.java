package com.example.employee.entity.schedule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("schedule_change_log")
public class ScheduleChangeLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("schedule_id")
    private Long scheduleId;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("schedule_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @TableField("change_type")
    private String changeType;

    @TableField("old_shift_id")
    private Long oldShiftId;

    @TableField("old_shift_code")
    private String oldShiftCode;

    @TableField("old_shift_name")
    private String oldShiftName;

    @TableField("new_shift_id")
    private Long newShiftId;

    @TableField("new_shift_code")
    private String newShiftCode;

    @TableField("new_shift_name")
    private String newShiftName;

    @TableField("change_reason")
    private String changeReason;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("changed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changedAt;
}
