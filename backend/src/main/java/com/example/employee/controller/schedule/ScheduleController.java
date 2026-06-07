package com.example.employee.controller.schedule;

import com.example.employee.common.Result;
import com.example.employee.dto.*;
import com.example.employee.entity.schedule.EmployeeSchedule;
import com.example.employee.service.schedule.EmployeeScheduleService;
import com.example.employee.service.schedule.ScheduleChangeLogService;
import com.example.employee.service.schedule.ScheduleExportService;
import com.example.employee.vo.ScheduleChangeLogVO;
import com.example.employee.vo.ScheduleWeekMatrixVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private EmployeeScheduleService scheduleService;

    @Autowired
    private ScheduleExportService exportService;

    @Autowired
    private ScheduleChangeLogService changeLogService;

    @GetMapping("/week-matrix")
    public Result<ScheduleWeekMatrixVO> getWeekMatrix(@RequestParam(required = false) String department,
                                                      @RequestParam(required = false) String scheduleWeek,
                                                      @RequestParam(required = false) String teamGroup) {
        return Result.success(scheduleService.getWeekMatrix(department, scheduleWeek, teamGroup));
    }

    @GetMapping("/list")
    public Result<List<EmployeeSchedule>> querySchedules(ScheduleQueryDTO dto) {
        return Result.success(scheduleService.querySchedules(dto));
    }

    @PutMapping("/update-single")
    public Result<EmployeeSchedule> updateSingleSchedule(@RequestBody ScheduleUpdateDTO dto,
                                                         @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                         @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long operatorId = userId != null ? userId : 1L;
        String operatorName = userName != null ? userName : "系统管理员";
        return Result.success(scheduleService.updateSingleSchedule(dto, operatorId, operatorName));
    }

    @PutMapping("/batch-update")
    public Result<Integer> batchUpdateSchedules(@RequestBody ScheduleBatchUpdateDTO dto,
                                                @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long operatorId = userId != null ? userId : 1L;
        String operatorName = userName != null ? userName : "系统管理员";
        return Result.success(scheduleService.batchUpdateSchedules(dto, operatorId, operatorName));
    }

    @PostMapping("/copy-week")
    public Result<Integer> copyWeekSchedule(@RequestBody ScheduleCopyDTO dto,
                                            @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long operatorId = userId != null ? userId : 1L;
        String operatorName = userName != null ? userName : "系统管理员";
        return Result.success(scheduleService.copyWeekSchedule(dto, operatorId, operatorName));
    }

    @PostMapping("/confirm")
    public Result<Integer> confirmSchedules(@RequestBody ScheduleConfirmDTO dto,
                                            @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long operatorId = userId != null ? userId : 1L;
        String operatorName = userName != null ? userName : "系统管理员";
        return Result.success(scheduleService.confirmSchedules(dto, operatorId, operatorName));
    }

    @PostMapping("/lock")
    public Result<Integer> lockSchedules(@RequestParam(required = false) String scheduleWeek,
                                         @RequestParam(required = false) String department) {
        return Result.success(scheduleService.lockSchedules(scheduleWeek, department));
    }

    @GetMapping("/change-logs")
    public Result<List<ScheduleChangeLogVO>> getChangeLogs(@RequestParam(required = false) Long employeeId,
                                                           @RequestParam(required = false) LocalDate startDate,
                                                           @RequestParam(required = false) LocalDate endDate,
                                                           @RequestParam(required = false) Long operatorId) {
        return Result.success(changeLogService.getChangeLogs(employeeId, startDate, endDate, operatorId));
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) String department,
                            @RequestParam(required = false) String scheduleWeek,
                            HttpServletResponse response) throws IOException {
        byte[] data = exportService.exportToExcel(department, scheduleWeek);
        String filename = "排班表_" + (scheduleWeek != null ? scheduleWeek : LocalDate.now().toString()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
