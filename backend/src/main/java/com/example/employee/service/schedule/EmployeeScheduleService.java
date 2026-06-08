package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.ScheduleBatchUpdateDTO;
import com.example.employee.dto.ScheduleConfirmDTO;
import com.example.employee.dto.ScheduleCopyDTO;
import com.example.employee.dto.ScheduleQueryDTO;
import com.example.employee.dto.ScheduleUpdateDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.schedule.EmployeeSchedule;
import com.example.employee.entity.schedule.ScheduleChangeLog;
import com.example.employee.entity.schedule.ScheduleChangeType;
import com.example.employee.entity.schedule.ScheduleStatus;
import com.example.employee.entity.schedule.ShiftDefinition;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.schedule.EmployeeScheduleMapper;
import com.example.employee.mapper.schedule.ScheduleChangeLogMapper;
import com.example.employee.service.EmployeeService;
import com.example.employee.vo.ScheduleAlertVO;
import com.example.employee.vo.ScheduleCellVO;
import com.example.employee.vo.ScheduleEmployeeRowVO;
import com.example.employee.vo.ScheduleWeekMatrixVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeScheduleService extends ServiceImpl<EmployeeScheduleMapper, EmployeeSchedule> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeScheduleService.class);

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ShiftDefinitionService shiftDefinitionService;

    @Autowired
    private ScheduleChangeLogMapper changeLogMapper;

    @Autowired
    private ScheduleAlertService alertService;

    public ScheduleWeekMatrixVO getWeekMatrix(String department, String scheduleWeek, String teamGroup) {
        ScheduleWeekMatrixVO vo = new ScheduleWeekMatrixVO();

        LocalDate weekStart;
        LocalDate weekEnd;
        if (scheduleWeek != null && !scheduleWeek.isEmpty()) {
            String[] parts = scheduleWeek.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            weekStart = LocalDate.of(year, 1, 1)
                    .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } else {
            weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            scheduleWeek = weekStart.get(IsoFields.WEEK_BASED_YEAR) + "-"
                    + String.format("%02d", weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
        }
        weekEnd = weekStart.plusDays(6);

        vo.setScheduleWeek(scheduleWeek);
        vo.setWeekStartDate(weekStart);
        vo.setWeekEndDate(weekEnd);

        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(weekStart.plusDays(i));
        }
        vo.setWeekDates(weekDates);

        LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
        if (department != null && !department.isEmpty()) {
            empWrapper.eq(Employee::getDepartment, department);
            vo.setDepartment(department);
        }
        List<Employee> employees = employeeMapper.selectList(empWrapper);

        LambdaQueryWrapper<EmployeeSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(EmployeeSchedule::getScheduleWeek, scheduleWeek);
        if (department != null && !department.isEmpty()) {
            scheduleWrapper.eq(EmployeeSchedule::getDepartment, department);
        }
        if (teamGroup != null && !teamGroup.isEmpty()) {
            scheduleWrapper.eq(EmployeeSchedule::getTeamGroup, teamGroup);
        }
        List<EmployeeSchedule> schedules = this.list(scheduleWrapper);

        Map<Long, Map<LocalDate, EmployeeSchedule>> scheduleMap = new HashMap<>();
        for (EmployeeSchedule s : schedules) {
            scheduleMap.computeIfAbsent(s.getEmployeeId(), k -> new HashMap<>())
                    .put(s.getScheduleDate(), s);
        }

        List<ShiftDefinition> shifts = department != null
                ? shiftDefinitionService.getShiftsByDepartment(department)
                : shiftDefinitionService.list();
        Map<Long, ShiftDefinition> shiftMap = shifts.stream()
                .collect(Collectors.toMap(ShiftDefinition::getId, s -> s));

        List<ScheduleAlertVO> alerts = alertService.getAlerts(department, weekStart, weekEnd, false);
        Map<Long, List<ScheduleAlertVO>> alertMap = alerts.stream()
                .collect(Collectors.groupingBy(ScheduleAlertVO::getEmployeeId));
        vo.setAlerts(alerts);

        boolean allConfirmed = !schedules.isEmpty();
        for (EmployeeSchedule s : schedules) {
            if (!ScheduleStatus.CONFIRMED.name().equals(s.getStatus())
                    && !ScheduleStatus.LOCKED.name().equals(s.getStatus())) {
                allConfirmed = false;
                break;
            }
        }
        vo.setStatus(allConfirmed ? ScheduleStatus.CONFIRMED.name() : ScheduleStatus.DRAFT.name());

        List<ScheduleEmployeeRowVO> rows = new ArrayList<>();
        for (Employee emp : employees) {
            ScheduleEmployeeRowVO row = new ScheduleEmployeeRowVO();
            row.setEmployeeId(emp.getId());
            row.setEmployeeName(emp.getName());
            row.setDepartment(emp.getDepartment());

            Map<String, ScheduleCellVO> cells = new HashMap<>();
            Map<LocalDate, EmployeeSchedule> empSchedules = scheduleMap.get(emp.getId());
            List<ScheduleAlertVO> empAlerts = alertMap.getOrDefault(emp.getId(), new ArrayList<>());

            for (LocalDate date : weekDates) {
                ScheduleCellVO cell = new ScheduleCellVO();
                cell.setScheduleDate(date);

                if (empSchedules != null && empSchedules.containsKey(date)) {
                    EmployeeSchedule s = empSchedules.get(date);
                    cell.setScheduleId(s.getId());
                    cell.setShiftId(s.getShiftId());
                    cell.setShiftCode(s.getShiftCode());
                    cell.setShiftName(s.getShiftName());
                    cell.setStartTime(s.getStartTime());
                    cell.setEndTime(s.getEndTime());
                    cell.setIsCrossDay(s.getIsCrossDay());
                    cell.setStatus(s.getStatus());
                    cell.setRemark(s.getRemark());
                    if (shiftMap.containsKey(s.getShiftId())) {
                        cell.setColor(shiftMap.get(s.getShiftId()).getColor());
                    }
                }

                boolean hasAlert = empAlerts.stream()
                        .anyMatch(a -> (a.getAlertDate() != null && a.getAlertDate().equals(date))
                                || (a.getAlertStartDate() != null && !date.isBefore(a.getAlertStartDate())
                                && a.getAlertEndDate() != null && !date.isAfter(a.getAlertEndDate())));
                cell.setHasAlert(hasAlert);

                cells.put(date.toString(), cell);
            }
            row.setScheduleCells(cells);
            rows.add(row);
        }
        vo.setEmployeeRows(rows);

        return vo;
    }

    @Transactional
    public EmployeeSchedule updateSingleSchedule(ScheduleUpdateDTO dto, Long operatorId, String operatorName) {
        Employee employee = employeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        ShiftDefinition shift = shiftDefinitionService.getById(dto.getShiftId());
        if (shift == null) {
            throw new RuntimeException("班次不存在");
        }

        LocalDate date = dto.getScheduleDate();
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String week = weekStart.get(IsoFields.WEEK_BASED_YEAR) + "-"
                + String.format("%02d", weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

        LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSchedule::getEmployeeId, dto.getEmployeeId())
                .eq(EmployeeSchedule::getScheduleDate, date);
        EmployeeSchedule existing = this.getOne(wrapper);

        Long oldShiftId = null;
        String oldShiftCode = null;
        String oldShiftName = null;

        if (existing != null) {
            if (ScheduleStatus.LOCKED.name().equals(existing.getStatus())) {
                throw new RuntimeException("排班已锁定，无法修改");
            }
            oldShiftId = existing.getShiftId();
            oldShiftCode = existing.getShiftCode();
            oldShiftName = existing.getShiftName();

            existing.setShiftId(dto.getShiftId());
            existing.setShiftCode(dto.getShiftCode() != null ? dto.getShiftCode() : shift.getShiftCode());
            existing.setShiftName(dto.getShiftName() != null ? dto.getShiftName() : shift.getShiftName());
            existing.setStartTime(dto.getStartTime() != null ? dto.getStartTime() : shift.getStartTime());
            existing.setEndTime(dto.getEndTime() != null ? dto.getEndTime() : shift.getEndTime());
            existing.setIsCrossDay(dto.getIsCrossDay() != null ? dto.getIsCrossDay() : shift.getIsCrossDay());
            existing.setRemark(dto.getRemark());
            existing.setUpdatedAt(LocalDateTime.now());
            this.updateById(existing);

            logChange(existing.getId(), dto.getEmployeeId(), employee.getName(), date,
                    ScheduleChangeType.UPDATE.name(), oldShiftId, oldShiftCode, oldShiftName,
                    existing.getShiftId(), existing.getShiftCode(), existing.getShiftName(),
                    dto.getChangeReason(), operatorId, operatorName);

            detectConflictsForEmployee(dto.getEmployeeId(), date.minusDays(3), date.plusDays(3), employee.getDepartment());

            return existing;
        } else {
            EmployeeSchedule schedule = new EmployeeSchedule();
            schedule.setScheduleWeek(week);
            schedule.setDepartment(employee.getDepartment());
            schedule.setEmployeeId(dto.getEmployeeId());
            schedule.setEmployeeName(employee.getName());
            schedule.setScheduleDate(date);
            schedule.setShiftId(dto.getShiftId());
            schedule.setShiftCode(dto.getShiftCode() != null ? dto.getShiftCode() : shift.getShiftCode());
            schedule.setShiftName(dto.getShiftName() != null ? dto.getShiftName() : shift.getShiftName());
            schedule.setStartTime(dto.getStartTime() != null ? dto.getStartTime() : shift.getStartTime());
            schedule.setEndTime(dto.getEndTime() != null ? dto.getEndTime() : shift.getEndTime());
            schedule.setIsCrossDay(dto.getIsCrossDay() != null ? dto.getIsCrossDay() : shift.getIsCrossDay());
            schedule.setStatus(ScheduleStatus.DRAFT.name());
            schedule.setRemark(dto.getRemark());
            schedule.setCreatedBy(operatorId);
            schedule.setCreatedByName(operatorName);
            schedule.setCreatedAt(LocalDateTime.now());
            schedule.setUpdatedAt(LocalDateTime.now());
            this.save(schedule);

            logChange(schedule.getId(), dto.getEmployeeId(), employee.getName(), date,
                    ScheduleChangeType.CREATE.name(), null, null, null,
                    schedule.getShiftId(), schedule.getShiftCode(), schedule.getShiftName(),
                    dto.getChangeReason(), operatorId, operatorName);

            detectConflictsForEmployee(dto.getEmployeeId(), date.minusDays(3), date.plusDays(3), employee.getDepartment());

            return schedule;
        }
    }

    @Transactional
    public int batchUpdateSchedules(ScheduleBatchUpdateDTO dto, Long operatorId, String operatorName) {
        List<Long> employeeIds = dto.getEmployeeIds();
        Map<Long, Employee> employeeMap;
        if (employeeIds == null || employeeIds.isEmpty()) {
            LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
            if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
                empWrapper.eq(Employee::getDepartment, dto.getDepartment());
            }
            List<Employee> emps = employeeMapper.selectList(empWrapper);
            employeeIds = emps.stream().map(Employee::getId).collect(Collectors.toList());
            employeeMap = emps.stream().collect(Collectors.toMap(Employee::getId, e -> e));
        } else {
            employeeMap = employeeService.getEmployeeMapByIds(employeeIds);
        }

        List<LocalDate> dates = dto.getDates();
        if (dates == null || dates.isEmpty()) {
            dates = new ArrayList<>();
            LocalDate start = dto.getStartDate();
            LocalDate end = dto.getEndDate();
            while (!start.isAfter(end)) {
                dates.add(start);
                start = start.plusDays(1);
            }
        }

        ShiftDefinition shift = shiftDefinitionService.getById(dto.getShiftId());
        if (shift == null) {
            throw new RuntimeException("班次不存在");
        }

        int count = 0;
        for (Long empId : employeeIds) {
            Employee emp = employeeMap.get(empId);
            if (emp == null) continue;

            for (LocalDate date : dates) {
                LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                String week = weekStart.get(IsoFields.WEEK_BASED_YEAR) + "-"
                        + String.format("%02d", weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

                LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(EmployeeSchedule::getEmployeeId, empId)
                        .eq(EmployeeSchedule::getScheduleDate, date);
                EmployeeSchedule existing = this.getOne(wrapper);

                Long oldShiftId = null;
                String oldShiftCode = null;
                String oldShiftName = null;

                if (existing != null) {
                    if (ScheduleStatus.LOCKED.name().equals(existing.getStatus())) {
                        continue;
                    }
                    oldShiftId = existing.getShiftId();
                    oldShiftCode = existing.getShiftCode();
                    oldShiftName = existing.getShiftName();

                    existing.setShiftId(dto.getShiftId());
                    existing.setShiftCode(dto.getShiftCode() != null ? dto.getShiftCode() : shift.getShiftCode());
                    existing.setShiftName(dto.getShiftName() != null ? dto.getShiftName() : shift.getShiftName());
                    existing.setStartTime(shift.getStartTime());
                    existing.setEndTime(shift.getEndTime());
                    existing.setIsCrossDay(shift.getIsCrossDay());
                    existing.setUpdatedAt(LocalDateTime.now());
                    this.updateById(existing);

                    logChange(existing.getId(), empId, emp.getName(), date,
                            ScheduleChangeType.BATCH_UPDATE.name(),
                            oldShiftId, oldShiftCode, oldShiftName,
                            existing.getShiftId(), existing.getShiftCode(), existing.getShiftName(),
                            dto.getChangeReason(), operatorId, operatorName);
                } else {
                    EmployeeSchedule schedule = new EmployeeSchedule();
                    schedule.setScheduleWeek(week);
                    schedule.setDepartment(emp.getDepartment());
                    schedule.setEmployeeId(empId);
                    schedule.setEmployeeName(emp.getName());
                    schedule.setTeamGroup(dto.getTeamGroup());
                    schedule.setScheduleDate(date);
                    schedule.setShiftId(dto.getShiftId());
                    schedule.setShiftCode(dto.getShiftCode() != null ? dto.getShiftCode() : shift.getShiftCode());
                    schedule.setShiftName(dto.getShiftName() != null ? dto.getShiftName() : shift.getShiftName());
                    schedule.setStartTime(shift.getStartTime());
                    schedule.setEndTime(shift.getEndTime());
                    schedule.setIsCrossDay(shift.getIsCrossDay());
                    schedule.setStatus(ScheduleStatus.DRAFT.name());
                    schedule.setCreatedBy(operatorId);
                    schedule.setCreatedByName(operatorName);
                    schedule.setCreatedAt(LocalDateTime.now());
                    schedule.setUpdatedAt(LocalDateTime.now());
                    this.save(schedule);

                    logChange(schedule.getId(), empId, emp.getName(), date,
                            ScheduleChangeType.BATCH_UPDATE.name(),
                            null, null, null,
                            schedule.getShiftId(), schedule.getShiftCode(), schedule.getShiftName(),
                            dto.getChangeReason(), operatorId, operatorName);
                }
                count++;
            }

            if (!dates.isEmpty()) {
                detectConflictsForEmployee(empId,
                        dates.get(0).minusDays(3),
                        dates.get(dates.size() - 1).plusDays(3),
                        emp.getDepartment());
            }
        }
        return count;
    }

    @Transactional
    public int copyWeekSchedule(ScheduleCopyDTO dto, Long operatorId, String operatorName) {
        String[] sourceParts = dto.getSourceWeek().split("-");
        int sYear = Integer.parseInt(sourceParts[0]);
        int sWeek = Integer.parseInt(sourceParts[1]);
        LocalDate sourceStart = LocalDate.of(sYear, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, sWeek)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        String[] targetParts = dto.getTargetWeek().split("-");
        int tYear = Integer.parseInt(targetParts[0]);
        int tWeek = Integer.parseInt(targetParts[1]);
        LocalDate targetStart = LocalDate.of(tYear, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, tWeek)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LambdaQueryWrapper<EmployeeSchedule> sourceWrapper = new LambdaQueryWrapper<>();
        sourceWrapper.eq(EmployeeSchedule::getScheduleWeek, dto.getSourceWeek());
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            sourceWrapper.eq(EmployeeSchedule::getDepartment, dto.getDepartment());
        }
        if (dto.getTeamGroup() != null && !dto.getTeamGroup().isEmpty()) {
            sourceWrapper.eq(EmployeeSchedule::getTeamGroup, dto.getTeamGroup());
        }
        List<EmployeeSchedule> sourceSchedules = this.list(sourceWrapper);

        int count = 0;
        for (EmployeeSchedule source : sourceSchedules) {
            int dayOffset = source.getScheduleDate().getDayOfWeek().getValue() - 1;
            LocalDate targetDate = targetStart.plusDays(dayOffset);

            LambdaQueryWrapper<EmployeeSchedule> existingWrapper = new LambdaQueryWrapper<>();
            existingWrapper.eq(EmployeeSchedule::getEmployeeId, source.getEmployeeId())
                    .eq(EmployeeSchedule::getScheduleDate, targetDate);
            EmployeeSchedule existing = this.getOne(existingWrapper);

            Long oldShiftId = null;
            String oldShiftCode = null;
            String oldShiftName = null;

            if (existing != null) {
                if (ScheduleStatus.LOCKED.name().equals(existing.getStatus())) {
                    continue;
                }
                oldShiftId = existing.getShiftId();
                oldShiftCode = existing.getShiftCode();
                oldShiftName = existing.getShiftName();

                existing.setShiftId(source.getShiftId());
                existing.setShiftCode(source.getShiftCode());
                existing.setShiftName(source.getShiftName());
                existing.setStartTime(source.getStartTime());
                existing.setEndTime(source.getEndTime());
                existing.setIsCrossDay(source.getIsCrossDay());
                existing.setTeamGroup(source.getTeamGroup());
                existing.setUpdatedAt(LocalDateTime.now());
                this.updateById(existing);

                logChange(existing.getId(), source.getEmployeeId(), source.getEmployeeName(), targetDate,
                        ScheduleChangeType.COPY.name(),
                        oldShiftId, oldShiftCode, oldShiftName,
                        existing.getShiftId(), existing.getShiftCode(), existing.getShiftName(),
                        dto.getChangeReason(), operatorId, operatorName);
            } else {
                EmployeeSchedule schedule = new EmployeeSchedule();
                schedule.setScheduleWeek(dto.getTargetWeek());
                schedule.setDepartment(source.getDepartment());
                schedule.setEmployeeId(source.getEmployeeId());
                schedule.setEmployeeName(source.getEmployeeName());
                schedule.setTeamGroup(source.getTeamGroup());
                schedule.setScheduleDate(targetDate);
                schedule.setShiftId(source.getShiftId());
                schedule.setShiftCode(source.getShiftCode());
                schedule.setShiftName(source.getShiftName());
                schedule.setStartTime(source.getStartTime());
                schedule.setEndTime(source.getEndTime());
                schedule.setIsCrossDay(source.getIsCrossDay());
                schedule.setEffectiveStartDate(targetDate);
                schedule.setEffectiveEndDate(targetDate);
                schedule.setStatus(ScheduleStatus.DRAFT.name());
                schedule.setCreatedBy(operatorId);
                schedule.setCreatedByName(operatorName);
                schedule.setCreatedAt(LocalDateTime.now());
                schedule.setUpdatedAt(LocalDateTime.now());
                this.save(schedule);

                logChange(schedule.getId(), source.getEmployeeId(), source.getEmployeeName(), targetDate,
                        ScheduleChangeType.COPY.name(),
                        null, null, null,
                        schedule.getShiftId(), schedule.getShiftCode(), schedule.getShiftName(),
                        dto.getChangeReason(), operatorId, operatorName);
            }
            count++;
        }

        LocalDate targetEnd = targetStart.plusDays(6);
        LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            empWrapper.eq(Employee::getDepartment, dto.getDepartment());
        }
        List<Employee> employees = employeeMapper.selectList(empWrapper);
        for (Employee emp : employees) {
            detectConflictsForEmployee(emp.getId(), targetStart, targetEnd, emp.getDepartment());
        }

        return count;
    }

    @Transactional
    public int confirmSchedules(ScheduleConfirmDTO dto, Long operatorId, String operatorName) {
        List<EmployeeSchedule> schedules;
        if (dto.getScheduleIds() != null && !dto.getScheduleIds().isEmpty()) {
            schedules = this.listByIds(dto.getScheduleIds());
        } else {
            LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
            if (dto.getScheduleWeek() != null && !dto.getScheduleWeek().isEmpty()) {
                wrapper.eq(EmployeeSchedule::getScheduleWeek, dto.getScheduleWeek());
            }
            if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
                wrapper.eq(EmployeeSchedule::getDepartment, dto.getDepartment());
            }
            wrapper.eq(EmployeeSchedule::getStatus, ScheduleStatus.DRAFT.name());
            schedules = this.list(wrapper);
        }

        int count = 0;
        for (EmployeeSchedule s : schedules) {
            if (ScheduleStatus.DRAFT.name().equals(s.getStatus())) {
                s.setStatus(ScheduleStatus.CONFIRMED.name());
                s.setConfirmedBy(operatorId);
                s.setConfirmedByName(operatorName);
                s.setConfirmedAt(LocalDateTime.now());
                s.setRemark(dto.getRemark());
                s.setUpdatedAt(LocalDateTime.now());
                this.updateById(s);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public int lockSchedules(String scheduleWeek, String department) {
        LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
        if (scheduleWeek != null && !scheduleWeek.isEmpty()) {
            wrapper.eq(EmployeeSchedule::getScheduleWeek, scheduleWeek);
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq(EmployeeSchedule::getDepartment, department);
        }
        wrapper.eq(EmployeeSchedule::getStatus, ScheduleStatus.CONFIRMED.name());
        List<EmployeeSchedule> schedules = this.list(wrapper);

        int count = 0;
        for (EmployeeSchedule s : schedules) {
            s.setStatus(ScheduleStatus.LOCKED.name());
            s.setUpdatedAt(LocalDateTime.now());
            this.updateById(s);
            count++;
        }
        return count;
    }

    public List<EmployeeSchedule> querySchedules(ScheduleQueryDTO dto) {
        LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            wrapper.eq(EmployeeSchedule::getDepartment, dto.getDepartment());
        }
        if (dto.getTeamGroup() != null && !dto.getTeamGroup().isEmpty()) {
            wrapper.eq(EmployeeSchedule::getTeamGroup, dto.getTeamGroup());
        }
        if (dto.getEmployeeId() != null) {
            wrapper.eq(EmployeeSchedule::getEmployeeId, dto.getEmployeeId());
        }
        if (dto.getScheduleWeek() != null && !dto.getScheduleWeek().isEmpty()) {
            wrapper.eq(EmployeeSchedule::getScheduleWeek, dto.getScheduleWeek());
        }
        if (dto.getStartDate() != null) {
            wrapper.ge(EmployeeSchedule::getScheduleDate, dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            wrapper.le(EmployeeSchedule::getScheduleDate, dto.getEndDate());
        }
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            wrapper.eq(EmployeeSchedule::getStatus, dto.getStatus());
        }
        wrapper.orderByAsc(EmployeeSchedule::getEmployeeId, EmployeeSchedule::getScheduleDate);
        return this.list(wrapper);
    }

    private void logChange(Long scheduleId, Long employeeId, String employeeName, LocalDate date,
                           String changeType, Long oldShiftId, String oldShiftCode, String oldShiftName,
                           Long newShiftId, String newShiftCode, String newShiftName,
                           String reason, Long operatorId, String operatorName) {
        ScheduleChangeLog log = new ScheduleChangeLog();
        log.setScheduleId(scheduleId);
        log.setEmployeeId(employeeId);
        log.setEmployeeName(employeeName);
        log.setScheduleDate(date);
        log.setChangeType(changeType);
        log.setOldShiftId(oldShiftId);
        log.setOldShiftCode(oldShiftCode);
        log.setOldShiftName(oldShiftName);
        log.setNewShiftId(newShiftId);
        log.setNewShiftCode(newShiftCode);
        log.setNewShiftName(newShiftName);
        log.setChangeReason(reason);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setChangedAt(LocalDateTime.now());
        changeLogMapper.insert(log);
    }

    public void detectConflictsForEmployee(Long employeeId, LocalDate startDate, LocalDate endDate, String department) {
        List<EmployeeSchedule> schedules = baseMapper.findByEmployeeAndDateRange(employeeId, startDate, endDate);
        if (schedules == null || schedules.isEmpty()) {
            return;
        }
        schedules.sort((a, b) -> a.getScheduleDate().compareTo(b.getScheduleDate()));

        Map<LocalDate, List<EmployeeSchedule>> byDate = schedules.stream()
                .collect(Collectors.groupingBy(EmployeeSchedule::getScheduleDate));
        for (Map.Entry<LocalDate, List<EmployeeSchedule>> entry : byDate.entrySet()) {
            if (entry.getValue().size() > 1) {
                alertService.createAlert(
                        "MULTI_SHIFT", "ERROR", department,
                        employeeId, entry.getValue().get(0).getEmployeeName(),
                        entry.getKey(), null, null,
                        entry.getKey() + " 存在多个排班冲突",
                        "共" + entry.getValue().size() + "个班次"
                );
            }
        }

        int consecutiveNights = 0;
        LocalDate nightStart = null;
        for (EmployeeSchedule s : schedules) {
            if ("NIGHT_SHIFT".equals(s.getShiftCode())) {
                if (consecutiveNights == 0) {
                    nightStart = s.getScheduleDate();
                }
                consecutiveNights++;
                if (consecutiveNights >= 3) {
                    alertService.createAlert(
                            "CONSECUTIVE_NIGHT", "WARNING", department,
                            employeeId, s.getEmployeeName(),
                            s.getScheduleDate(), nightStart, s.getScheduleDate(),
                            "连续" + consecutiveNights + "天大夜班，请关注休息情况",
                            "超限连续天数: " + consecutiveNights
                    );
                }
            } else {
                consecutiveNights = 0;
                nightStart = null;
            }
        }

        for (int i = 1; i < schedules.size(); i++) {
            EmployeeSchedule prev = schedules.get(i - 1);
            EmployeeSchedule curr = schedules.get(i);

            if (prev.getEndTime() == null || curr.getStartTime() == null) continue;

            long hoursBetween = java.time.Duration.between(
                    prev.getScheduleDate().atTime(prev.getEndTime()),
                    curr.getScheduleDate().atTime(curr.getStartTime())
            ).toHours();

            if (prev.getIsCrossDay() != null && prev.getIsCrossDay()) {
                hoursBetween = java.time.Duration.between(
                        prev.getScheduleDate().plusDays(1).atTime(prev.getEndTime()),
                        curr.getScheduleDate().atTime(curr.getStartTime())
                ).toHours();
            }

            if (hoursBetween < 11 && !"REST".equals(prev.getShiftCode())) {
                alertService.createAlert(
                        "REST_INTERVAL", "WARNING", department,
                        employeeId, curr.getEmployeeName(),
                        curr.getScheduleDate(), prev.getScheduleDate(), curr.getScheduleDate(),
                        "班次间休息间隔不足，仅" + hoursBetween + "小时（建议≥11小时）",
                        "前班:" + prev.getShiftName() + " 后班:" + curr.getShiftName()
                );
            }
        }
    }
}
