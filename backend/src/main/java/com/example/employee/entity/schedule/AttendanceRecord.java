package com.example.employee.entity.schedule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance_record")
public class AttendanceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    private String department;

    @TableField("attendance_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @TableField("schedule_id")
    private Long scheduleId;

    @TableField("shift_id")
    private Long shiftId;

    @TableField("shift_code")
    private String shiftCode;

    @TableField("punch_in_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime punchInTime;

    @TableField("punch_out_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime punchOutTime;

    @TableField("work_hours")
    private BigDecimal workHours;

    @TableField("is_late")
    private Boolean isLate;

    @TableField("late_minutes")
    private Integer lateMinutes;

    @TableField("is_early_leave")
    private Boolean isEarlyLeave;

    @TableField("early_leave_minutes")
    private Integer earlyLeaveMinutes;

    @TableField("is_absent")
    private Boolean isAbsent;

    @TableField("is_abnormal")
    private Boolean isAbnormal;

    @TableField("abnormal_type")
    private String abnormalType;

    @TableField("abnormal_reason")
    private String abnormalReason;

    private String remark;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
