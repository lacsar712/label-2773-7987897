package com.example.employee.entity.schedule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("employee_schedule")
public class EmployeeSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("schedule_week")
    private String scheduleWeek;

    private String department;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("team_group")
    private String teamGroup;

    @TableField("schedule_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @TableField("shift_id")
    private Long shiftId;

    @TableField("shift_code")
    private String shiftCode;

    @TableField("shift_name")
    private String shiftName;

    @TableField("start_time")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @TableField("end_time")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @TableField("is_cross_day")
    private Boolean isCrossDay;

    @TableField("effective_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveStartDate;

    @TableField("effective_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveEndDate;

    private String status;

    @TableField("confirmed_by")
    private Long confirmedBy;

    @TableField("confirmed_by_name")
    private String confirmedByName;

    @TableField("confirmed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmedAt;

    private String remark;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_by_name")
    private String createdByName;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
