package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.entity.Employee;
import com.example.employee.entity.message.MessageEventType;
import com.example.employee.entity.schedule.EmployeeSchedule;
import com.example.employee.entity.schedule.ScheduleStatus;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.schedule.EmployeeScheduleMapper;
import com.example.employee.service.message.SysMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleReminderScheduler.class);

    @Autowired
    private EmployeeScheduleMapper scheduleMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private SysMessageService sysMessageService;

    @Scheduled(cron = "0 0 9 ? * MON")
    public void remindUnconfirmedSchedules() {
        logger.info("开始执行未确认排班提醒任务");

        LocalDate thisWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String thisWeek = thisWeekStart.get(IsoFields.WEEK_BASED_YEAR) + "-"
                + String.format("%02d", thisWeekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

        LocalDate nextWeekStart = thisWeekStart.plusWeeks(1);
        String nextWeek = nextWeekStart.get(IsoFields.WEEK_BASED_YEAR) + "-"
                + String.format("%02d", nextWeekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

        remindForWeek(thisWeek, "本周");
        remindForWeek(nextWeek, "下周");

        logger.info("未确认排班提醒任务执行完成");
    }

    private void remindForWeek(String week, String weekLabel) {
        LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSchedule::getScheduleWeek, week)
                .eq(EmployeeSchedule::getStatus, ScheduleStatus.DRAFT.name());
        List<EmployeeSchedule> unconfirmed = scheduleMapper.selectList(wrapper);

        if (unconfirmed.isEmpty()) {
            logger.info("{}({})排班已全部确认，无需提醒", weekLabel, week);
            return;
        }

        Map<String, List<EmployeeSchedule>> byDept = unconfirmed.stream()
                .collect(Collectors.groupingBy(EmployeeSchedule::getDepartment));

        List<Employee> allManagers = findDepartmentManagers();
        Map<String, Employee> deptManagerMap = new HashMap<>();
        for (Employee mgr : allManagers) {
            deptManagerMap.put(mgr.getDepartment(), mgr);
        }

        for (Map.Entry<String, List<EmployeeSchedule>> entry : byDept.entrySet()) {
            String dept = entry.getKey();
            int count = entry.getValue().size();
            Employee manager = deptManagerMap.get(dept);

            if (manager != null) {
                String title = weekLabel + "排班待确认提醒";
                String summary = dept + weekLabel + "(" + week + ")尚有" + count + "条排班未确认，请及时处理";
                sysMessageService.sendMessage(
                        manager.getId(),
                        MessageEventType.PERFORMANCE_REMIND,
                        title,
                        summary,
                        "SCHEDULE",
                        week,
                        "/schedule?week=" + week
                );
                logger.info("已向{}({})发送排班提醒: {}", manager.getName(), dept, summary);
            } else {
                logger.warn("部门{}未找到负责人，跳过{}排班提醒", dept, weekLabel);
            }
        }
    }

    private List<Employee> findDepartmentManagers() {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Employee::getRole, "经理")
                .or()
                .like(Employee::getRole, "主管")
                .or()
                .like(Employee::getRole, "总监")
                .or()
                .like(Employee::getRole, "HRBP");
        List<Employee> managers = employeeMapper.selectList(wrapper);

        if (managers.isEmpty()) {
            managers = employeeMapper.selectList(new LambdaQueryWrapper<Employee>()
                    .eq(Employee::getDepartment, "人力资源部"));
        }
        return managers;
    }
}
