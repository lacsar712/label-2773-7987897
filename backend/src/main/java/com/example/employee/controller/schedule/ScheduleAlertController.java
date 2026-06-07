package com.example.employee.controller.schedule;

import com.example.employee.common.Result;
import com.example.employee.service.schedule.ScheduleAlertService;
import com.example.employee.vo.ScheduleAlertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule/alerts")
@CrossOrigin(origins = "*")
public class ScheduleAlertController {

    @Autowired
    private ScheduleAlertService alertService;

    @GetMapping
    public Result<List<ScheduleAlertVO>> getAlerts(@RequestParam(required = false) String department,
                                                    @RequestParam(required = false) LocalDate startDate,
                                                    @RequestParam(required = false) LocalDate endDate,
                                                    @RequestParam(required = false) Boolean isResolved) {
        return Result.success(alertService.getAlerts(department, startDate, endDate, isResolved));
    }

    @PostMapping("/{id}/resolve")
    public Result<Boolean> resolveAlert(@PathVariable Long id,
                                        @RequestParam String note,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long operatorId = userId != null ? userId : 1L;
        String operatorName = userName != null ? userName : "系统管理员";
        return Result.success(alertService.resolveAlert(id, operatorId, operatorName, note));
    }
}
