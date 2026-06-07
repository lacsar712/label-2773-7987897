package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.AttendancePunchDTO;
import com.example.employee.dto.AttendanceQueryDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.schedule.AttendanceRecord;
import com.example.employee.entity.schedule.EmployeeSchedule;
import com.example.employee.entity.schedule.ShiftDefinition;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.schedule.AttendanceRecordMapper;
import com.example.employee.mapper.schedule.EmployeeScheduleMapper;
import com.example.employee.vo.AttendanceCompareVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord> {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeScheduleMapper scheduleMapper;

    @Autowired
    private ShiftDefinitionService shiftDefinitionService;

    @Transactional
    public AttendanceRecord punchInOut(AttendancePunchDTO dto) {
        Employee emp = employeeMapper.selectById(dto.getEmployeeId());
        if (emp == null) {
            throw new RuntimeException("员工不存在");
        }

        LocalDate date = dto.getPunchTime().toLocalDate();

        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceRecord::getEmployeeId, dto.getEmployeeId())
                .eq(AttendanceRecord::getAttendanceDate, date);
        AttendanceRecord record = this.getOne(wrapper);

        LambdaQueryWrapper<EmployeeSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(EmployeeSchedule::getEmployeeId, dto.getEmployeeId())
                .eq(EmployeeSchedule::getScheduleDate, date);
        EmployeeSchedule schedule = scheduleMapper.selectOne(scheduleWrapper);

        LocalTime shiftStartTime = null;
        LocalTime shiftEndTime = null;
        boolean isCrossDay = false;

        if (schedule != null) {
            shiftStartTime = schedule.getStartTime();
            shiftEndTime = schedule.getEndTime();
            isCrossDay = schedule.getIsCrossDay() != null && schedule.getIsCrossDay();
        }

        if (record == null) {
            record = new AttendanceRecord();
            record.setEmployeeId(dto.getEmployeeId());
            record.setEmployeeName(emp.getName());
            record.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : emp.getDepartment());
            record.setAttendanceDate(date);
            record.setPunchInTime(dto.getPunchTime());
            record.setIsLate(false);
            record.setIsEarlyLeave(false);
            record.setIsAbsent(false);
            record.setIsAbnormal(false);
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());

            if (schedule != null) {
                record.setScheduleId(schedule.getId());
                record.setShiftId(schedule.getShiftId());
                record.setShiftCode(schedule.getShiftCode());

                if (shiftStartTime != null) {
                    LocalTime punchInLocal = dto.getPunchTime().toLocalTime();
                    long lateMinutes = ChronoUnit.MINUTES.between(shiftStartTime, punchInLocal);
                    if (lateMinutes > 0) {
                        record.setIsLate(true);
                        record.setLateMinutes((int) lateMinutes);
                        record.setIsAbnormal(true);
                        record.setAbnormalType("LATE");
                        record.setAbnormalReason("迟到" + lateMinutes + "分钟");
                    }
                }
            }
            this.save(record);
        } else {
            record.setPunchOutTime(dto.getPunchTime());

            if (record.getPunchInTime() != null && record.getPunchOutTime() != null) {
                long minutes = ChronoUnit.MINUTES.between(record.getPunchInTime(), record.getPunchOutTime());
                BigDecimal hours = BigDecimal.valueOf(minutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                record.setWorkHours(hours);
            }

            if (schedule != null && shiftEndTime != null && record.getPunchOutTime() != null) {
                LocalTime punchOutLocal = record.getPunchOutTime().toLocalTime();
                long earlyMinutes;
                if (isCrossDay) {
                    if (punchOutLocal.isAfter(LocalTime.MIDNIGHT) && punchOutLocal.isBefore(shiftEndTime)) {
                        earlyMinutes = ChronoUnit.MINUTES.between(punchOutLocal, shiftEndTime);
                    } else {
                        earlyMinutes = 0;
                    }
                } else {
                    earlyMinutes = ChronoUnit.MINUTES.between(punchOutLocal, shiftEndTime);
                }
                if (earlyMinutes > 0) {
                    record.setIsEarlyLeave(true);
                    record.setEarlyLeaveMinutes((int) earlyMinutes);
                    record.setIsAbnormal(true);
                    if (record.getAbnormalType() == null) {
                        record.setAbnormalType("EARLY_LEAVE");
                        record.setAbnormalReason("早退" + earlyMinutes + "分钟");
                    } else {
                        record.setAbnormalType(record.getAbnormalType() + ",EARLY_LEAVE");
                        record.setAbnormalReason(record.getAbnormalReason() + "; 早退" + earlyMinutes + "分钟");
                    }
                }
            }
            record.setUpdatedAt(LocalDateTime.now());
            this.updateById(record);
        }
        return record;
    }

    public List<AttendanceCompareVO> queryAttendance(AttendanceQueryDTO dto) {
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            wrapper.eq(AttendanceRecord::getDepartment, dto.getDepartment());
        }
        if (dto.getEmployeeId() != null) {
            wrapper.eq(AttendanceRecord::getEmployeeId, dto.getEmployeeId());
        }
        if (dto.getStartDate() != null) {
            wrapper.ge(AttendanceRecord::getAttendanceDate, dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            wrapper.le(AttendanceRecord::getAttendanceDate, dto.getEndDate());
        }
        if (dto.getIsAbnormal() != null) {
            wrapper.eq(AttendanceRecord::getIsAbnormal, dto.getIsAbnormal());
        }
        if (dto.getAbnormalType() != null && !dto.getAbnormalType().isEmpty()) {
            wrapper.like(AttendanceRecord::getAbnormalType, dto.getAbnormalType());
        }
        wrapper.orderByDesc(AttendanceRecord::getAttendanceDate, AttendanceRecord::getEmployeeId);

        List<AttendanceRecord> records = this.list(wrapper);
        return convertToCompareVOList(records);
    }

    private List<AttendanceCompareVO> convertToCompareVOList(List<AttendanceRecord> records) {
        if (records == null) {
            return new ArrayList<>();
        }
        return records.stream().map(this::convertToCompareVO).collect(Collectors.toList());
    }

    private AttendanceCompareVO convertToCompareVO(AttendanceRecord record) {
        AttendanceCompareVO vo = new AttendanceCompareVO();
        BeanUtils.copyProperties(record, vo);
        if (record.getScheduleId() != null) {
            EmployeeSchedule schedule = scheduleMapper.selectById(record.getScheduleId());
            if (schedule != null) {
                vo.setShiftName(schedule.getShiftName());
                vo.setScheduledStartTime(schedule.getStartTime());
                vo.setScheduledEndTime(schedule.getEndTime());
            } else if (record.getShiftId() != null) {
                ShiftDefinition shift = shiftDefinitionService.getById(record.getShiftId());
                if (shift != null) {
                    vo.setShiftName(shift.getShiftName());
                    vo.setScheduledStartTime(shift.getStartTime());
                    vo.setScheduledEndTime(shift.getEndTime());
                }
            }
        }
        return vo;
    }
}
