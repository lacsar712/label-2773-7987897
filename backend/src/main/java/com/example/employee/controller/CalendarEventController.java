package com.example.employee.controller;

import com.example.employee.common.Result;
import com.example.employee.dto.EventQueryDTO;
import com.example.employee.dto.ExportQueryDTO;
import com.example.employee.entity.CalendarEvent;
import com.example.employee.entity.CalendarSubscription;
import com.example.employee.service.CalendarEventService;
import com.example.employee.service.CalendarSubscriptionService;
import com.example.employee.service.ICalExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar/events")
@CrossOrigin(origins = "*")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    @Autowired
    private CalendarSubscriptionService calendarSubscriptionService;

    @Autowired
    private ICalExportService iCalExportService;

    @PostMapping("/query")
    public Result<List<CalendarEvent>> query(@RequestBody EventQueryDTO query) {
        LocalDateTime startTime = query.getStartTime() != null
                ? query.getStartTime()
                : LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endTime = query.getEndTime() != null
                ? query.getEndTime()
                : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
        return Result.success(calendarEventService.findByTimeRangeAndFilters(
                startTime, endTime, query.getEventTypes(), query.getEmployeeIds()));
    }

    @GetMapping("/date/{date}")
    public Result<List<CalendarEvent>> getByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);
        return Result.success(calendarEventService.findByDate(start, end));
    }

    @GetMapping("/{id}")
    public Result<CalendarEvent> getById(@PathVariable Long id) {
        return Result.success(calendarEventService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody @Valid CalendarEvent event) {
        return Result.success(calendarEventService.save(event));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody @Valid CalendarEvent event) {
        return Result.success(calendarEventService.updateById(event));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(calendarEventService.removeById(id));
    }

    @PostMapping("/sync")
    public Result<Boolean> syncFromSource(@RequestBody @Valid CalendarEvent event) {
        return Result.success(calendarEventService.syncFromSource(event));
    }

    @DeleteMapping("/sync/{sourceModule}/{sourceId}")
    public Result<Boolean> deleteBySource(
            @PathVariable String sourceModule,
            @PathVariable String sourceId) {
        return Result.success(calendarEventService.deleteBySource(sourceModule, sourceId));
    }

    @GetMapping("/event-types")
    public Result<List<Map<String, String>>> getEventTypes() {
        List<Map<String, String>> types = List.of(
                createTypeMap("LEAVE", "请假", "#FF7875"),
                createTypeMap("ONBOARDING", "入职", "#36CFC9"),
                createTypeMap("ANNIVERSARY", "司龄", "#FFC53D"),
                createTypeMap("DEPT_ACTIVITY", "部门活动", "#69C0FF"),
                createTypeMap("HOLIDAY", "节假日", "#95DE64"),
                createTypeMap("INTERVIEW", "面试", "#B37FEB"),
                createTypeMap("CUSTOM", "自定义", "#F759AB")
        );
        return Result.success(types);
    }

    @PostMapping("/export")
    public void exportICal(@RequestBody ExportQueryDTO query, HttpServletResponse response) throws IOException {
        LocalDateTime startTime = query.getStartTime() != null
                ? query.getStartTime()
                : LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endTime = query.getEndTime() != null
                ? query.getEndTime()
                : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

        List<CalendarEvent> events = calendarEventService.findByTimeRangeAndFilters(
                startTime, endTime, query.getEventTypes(), query.getEmployeeIds());

        String iCalContent = iCalExportService.exportToICal(events, query.getCalendarName());

        String fileName = (query.getCalendarName() != null ? query.getCalendarName() : "team-calendar") + ".ics";
        response.setContentType("text/calendar; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
        response.getWriter().write(iCalContent);
        response.getWriter().flush();
    }

    @GetMapping("/subscriptions/{subscriberId}")
    public Result<List<CalendarSubscription>> getSubscriptions(@PathVariable Long subscriberId) {
        return Result.success(calendarSubscriptionService.findBySubscriberId(subscriberId));
    }

    @PostMapping("/subscriptions")
    public Result<Boolean> addSubscription(@RequestBody CalendarSubscription subscription) {
        if (calendarSubscriptionService.exists(subscription.getSubscriberId(), subscription.getTargetEmployeeId())) {
            return Result.success(true);
        }
        return Result.success(calendarSubscriptionService.save(subscription));
    }

    @DeleteMapping("/subscriptions/{subscriberId}/{targetEmployeeId}")
    public Result<Boolean> removeSubscription(
            @PathVariable Long subscriberId,
            @PathVariable Long targetEmployeeId) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("subscriber_id", subscriberId);
        columnMap.put("target_employee_id", targetEmployeeId);
        return Result.success(calendarSubscriptionService.removeByMap(columnMap));
    }

    private Map<String, String> createTypeMap(String value, String label, String color) {
        Map<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        map.put("color", color);
        return map;
    }
}
